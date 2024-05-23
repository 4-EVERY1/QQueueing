package com.qqueueing.main.waiting.service;


import com.qqueueing.main.registration.model.GetWaitingInfoResDto;
import com.qqueueing.main.registration.model.Registration;
import com.qqueueing.main.registration.repository.RegistrationRepository;
import com.qqueueing.main.waiting.model.BatchResDto;
import com.qqueueing.main.waiting.model.GetMyOrderResDto;
import com.qqueueing.main.waiting.model.WaitingStatusDto;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Transactional
@Service
public class WaitingService {

    @Value("${servers.parsing}")
    private String serverUrl;

    private final ConsumerConnector consumerConnector;
    private final TargetApiConnector targetApiConnector;
    private final EnterProducer enterProducer;
    private final RegistrationRepository registrationRepository;

    private Set<Integer> activePartitions = new HashSet<>();
    private Map<Integer, WaitingStatusDto> queues = new HashMap<>();
    private Map<String, Integer> partitionNoMapper = new HashMap<>();
    private Map<String, String> targetUrlMapper = new HashMap<>();
    private final KafkaTopicManager kafkaTopicManager;
    private final String SERVER_ORIGIN;
    private final String QUEUE_PAGE_API = "/waiting/queue-page";
    private final String TARGET_PAGE_URI = "/waiting/page-req";
    private final String QUEUE_PAGE_FRONT;
    private final int TOKEN_LEN = 20;
    private final String TOPIC_NAME;
    private final String REPLACE_URL;
    // for test
    @Setter
    private String endpoint = "/waiting";
    private final String TEST_IP = "ipStringValueForTest";
    private final String TEST_TOKEN = "testTokenStringValue";
    private final String TEST_TARGET_URL;

    public WaitingService(ConsumerConnector consumerConnector, TargetApiConnector targetApiConnector,
                          EnterProducer enterProducer, RegistrationRepository registrationRepository, KafkaTopicManager kafkaTopicManager,
                          @Value("${servers.front}") String queuePageFront,
                          @Value("${servers.main}") String serverOrigin,
                          @Value("${kafka.topics.enter.name}") String topicName,
                          @Value("${servers.replace-url}") String replaceUrl,
                          @Value("${testing.target-url}") String testTargetUrl) {
        this.consumerConnector = consumerConnector;
        this.targetApiConnector = targetApiConnector;
        this.enterProducer = enterProducer; // init every partitions
        this.registrationRepository = registrationRepository;
        this.kafkaTopicManager = kafkaTopicManager;
        this.QUEUE_PAGE_FRONT = "http://" + queuePageFront;
        this.SERVER_ORIGIN = serverOrigin;
        this.TOPIC_NAME = topicName;
        this.REPLACE_URL = replaceUrl;
        this.TEST_TARGET_URL = testTargetUrl;
    }

    public GetWaitingInfoResDto getWaitingInfo(int partitionNo) {
        WaitingStatusDto waitingStatusDto = queues.get(partitionNo);
//        return new GetWaitingInfoResDto(waitingStatusDto.getEnterCntCapture(), waitingStatusDto.getTotalQueueSize());
        // 관리 페이지에서 보이는 totalQueueSize는 '실제로 대기열에 몇 명이 남아있는지' 여야 한다.
        return new GetWaitingInfoResDto(waitingStatusDto.getEnterCntCapture(),
                enterProducer.getLastEnteredIdx(partitionNo)
                        - (waitingStatusDto.getEnterCnt().get() + waitingStatusDto.getOutCnt().get()));
    }

    private void checkTopic() {
        Set<String> topics = kafkaTopicManager.getTopics();
        // for Test -- init every time main server restart
        // disable to remove kafka topic without restart zookeeper-server, kafka-broker now
//        if (topics.contains(TOPIC_NAME)) {
//            kafkaTopicManager.deleteTopic(TOPIC_NAME);
//        }
        if (!topics.contains(TOPIC_NAME)) {
            log.info("topic {} not present, create...", TOPIC_NAME);
            kafkaTopicManager.createTopic(TOPIC_NAME);
        }
    }

    /**
     * kafka producing test
     */
    public Object send(Integer partitionNo, long key, String message) {
        return enterProducer.send(message, key, partitionNo);
    }

    /**
     * 대기열 서버가 비정상 종료 후 다시 켜질 때, 대기열을 다시 활성화
     */
    @PostConstruct
    public void initQueues() {
        // init kafka topic
        checkTopic(); // 카프카 토픽 (있으면 지우고) 생성
        enterProducer.initialize(); // 토픽 내 0~19번 파티션까지 초기화

        List<Registration> queues = registrationRepository.findAll();
        queues.forEach(r -> {
                    partitionNoMapper.put(r.getTargetUrl(), r.getPartitionNo());
                    if (r.getIsActive()) activate(r);
                });
    }

    public void addUrlPartitionMapping(Registration registration) {
        partitionNoMapper.put(registration.getTargetUrl(), registration.getPartitionNo());
    }

    /**
     * 대기열 실행 중 상태 변경 시, 호출 필요
     */
    public void activate(Registration registration) {
        int partitionNo = registration.getPartitionNo();
        activePartitions.add(partitionNo);
        queues.put(partitionNo, new WaitingStatusDto(partitionNo, registration.getTargetUrl(), 0, 0));
        // update mongodb data
        registration.setIsActive(true);
        registrationRepository.save(registration);
//        // re-init partition
        boolean initSuccess = consumerConnector.clearPartition(partitionNo);
        if (!initSuccess) {
            removeInMemoryQueueInfo(partitionNo);
            log.error("activation failed!! marked as needRestart partition. partitionNumber {}", partitionNo);
            return;
        }
        log.info("partitionNumber {} ....activation end.", partitionNo);
        // re-init auto increment client order
        enterProducer.activate(partitionNo);
    }

//    @Async
//    @Scheduled(cron = "0/10 * * * * *") // 10초마다
//    public void checkPartitionsValid() {
//        registrationRepository.findAll()
//                .forEach(p -> {
//                    if (p.getIsActive() && !queues.containsKey(p.getPartitionNo())) {
//                        log.info("invalid partition exist! re-init... partitionNo = {}", p.getPartitionNo());
//                        activate(p);
//                    }
//                });
//    }

    public void activate(int partitionNo) {
        Registration registration = registrationRepository.findByPartitionNo(partitionNo);
        activate(registration);
    }

    /**
     * 대기열 실행 중 상태 변경 시, 호출 필요
     * @param partitionNo
     */
    public void deactivate(int partitionNo) {
        if (!activePartitions.contains(partitionNo)) {
            return;
        }
        Registration registration = registrationRepository.findByPartitionNo(partitionNo);

        removeInMemoryQueueInfo(partitionNo);

        // update mongodb data
        registration.setIsActive(false);
        registrationRepository.save(registration);
    }

    public void removeInMemoryQueueInfo(int partitionNo) {
        activePartitions.remove(partitionNo);
        queues.remove(partitionNo);
    }

    /**
     * 대기열 적용된 요청 시, 처음 거치는 메소드
     * @param targetUrl
     * @return redirect url; queue-page req api url || target-page req api url
     */
    public URI enter(String targetUrl) {
//        log.info("targetUrl = {}", targetUrl);
//        log.info("activePartitions = {}", activePartitions);
        Integer partitionNo = partitionNoMapper.get(targetUrl);
        if (partitionNo == null) {
//            log.error("wrong target url; proxy path applied to wrong target");
        }
//        log.info("partitionNo = {}", partitionNo);
        UriComponentsBuilder uriBuilder;

        if (activePartitions.contains(partitionNo)) { // 대기 필요
//            log.info("대기 필요 - 대기 페이지로 redirect 응답 반환");
            return URI.create(SERVER_ORIGIN + QUEUE_PAGE_API + "?Target-URL=" + targetUrl);
        } else { // 대기 불필요
//            log.info("대기 불필요 - 타겟 페이지로 redirect 응답 반환");
            String tempToken = createTempToken(targetUrl); // 토큰 생성
            log.info("tempToken = {}", tempToken);
            log.info("mapped targetUrl = {}", targetUrlMapper.get(tempToken));
            log.info("targetUrl = {}", targetUrl);

            uriBuilder = UriComponentsBuilder.fromUriString(SERVER_ORIGIN + TARGET_PAGE_URI)
                    .queryParam("token", tempToken);
            return uriBuilder.build().toUri();
        }
    }

    public String getQueuePage(String targetUrl) {
        Integer partitionNo = partitionNoMapper.get(targetUrl);
        WaitingStatusDto waitingStatusDto = queues.get(partitionNo);
        if (waitingStatusDto == null) {
            throw new RuntimeException("wrong targetUrl");
        }

//        log.info("targetUrl = {}", targetUrl);
        String html = targetApiConnector.forwardToWaitingPage(QUEUE_PAGE_FRONT, targetUrl).getBody();
        return parseHtmlPage(QUEUE_PAGE_FRONT, html);
    }

    /**
     * 실제 대기열 진입 메소드. 항상 대기가 필요한 경우에만 들어옴
     * @param targetUrl
     * @return
     */
    public Object enqueue(String targetUrl, HttpServletRequest request) {
        Integer partitionNo = partitionNoMapper.get(targetUrl);

//         카프카에 요청자 ip 저장 후, 대기 정보 반환
        return enterProducer.send(extractClientIp(request), (long) partitionNo, partitionNo);
    }


    private String createTempToken(String targetUrl) {
        String tempToken = RandomStringUtils.randomAlphanumeric(TOKEN_LEN);
        while (targetUrlMapper.get(tempToken) != null) {
            tempToken = RandomStringUtils.randomAlphanumeric(TOKEN_LEN);
        }
        targetUrlMapper.put(tempToken, targetUrl);
        return tempToken;
    }

    private String extractClientIp(HttpServletRequest request) {
        // 요청이 프록시 등을 거쳤을 때, 원래 ip주소를 담는 헤더
        String ip = request.getHeader("X-Forwarded-For");
//        log.info("client ip(X-Forwarded-For) = {}", ip);
        if (ip != null) return ip;

        String remoteAddr = request.getRemoteAddr();
//        log.info("request.remoteAddr = {}", remoteAddr);
        return remoteAddr;
    }

    public void out(int partitionNo, Long order) {
        // outList는 '내 앞에 나간 사람 수' 를 알기 위해 쓰이므로, 정렬된 채로 유지
        try {
            WaitingStatusDto waitingStatusDto = queues.get(partitionNo);
            List<Long> outList = waitingStatusDto.getOutList();
            int insertIdx = !outList.isEmpty() ? -(Collections.binarySearch(outList, order) + 1) : 0;
            outList.add(insertIdx, order);
            waitingStatusDto.getOutCnt().incrementAndGet();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GetMyOrderResDto getMyOrder(int partitionNo, Long oldOrder, String ip) {
        WaitingStatusDto waitingStatus = queues.get(partitionNo);
        Set<String> doneSet = waitingStatus.getDoneSet();
        List<Long> outList = waitingStatus.getOutList();
        int totalQueueSize = waitingStatus.getTotalQueueSize();
        long currentOffset = waitingStatus.getCurrentOffset();
        int outCntInFront = (-Collections.binarySearch(outList, oldOrder)) - 1;
        Long myOrder = Math.max(oldOrder - outCntInFront - currentOffset, 1); // newOrder // myOrder가 0 이하로 표시되는 상황을 방지해야 하므로
//        if (myOrder > enterProducer.getLastEnteredIdx(partitionNo)) {
//            log.error("lastEnteredIdx if greater than myOrder");
//        }
//        if (totalQueueSize < myOrder) {
//            log.error("==================Invalid Status!!! totalQueueSize must be greater than myOrder!!============");
//            log.info("totalQueueSize = {}", totalQueueSize);
//            log.info("currentOffset = {}", currentOffset);
//            log.info("outCntInFront = {} ------- need negative value check", outCntInFront);
//            log.info("myOrder = {}", myOrder);
//            log.info("oldOrder = {}", oldOrder);
//            log.info("============================ End ====================================");
//        }
        GetMyOrderResDto result = new GetMyOrderResDto(myOrder, totalQueueSize,
                waitingStatus.getEnterCntOfLastTime());
        result.update(oldOrder, outCntInFront, currentOffset);

//        if (doneSet.contains(ip)) { // waiting done
        if (ip.equals(TEST_IP) || doneSet.contains(ip)) { // waiting done // for test
//            log.info("ip addr {} requested, and return tempToken", ip);
            doneSet.remove(ip);
            result.setToken(createTempToken(waitingStatus.getTargetUrl()));
        }
        return result;
    }

    /**
     * 타겟 프론트 페이지 포워딩 메소드
     */
    public String forward(String token) {
        String targetUrl = targetUrlMapper.get(token);
        log.info("in forward()..........");
        log.info("token = {}", token);
        log.info("mapped targetUrl = {}", targetUrl);
        // for test
        if (token.equals(TEST_TOKEN)) {
            targetUrl = TEST_TARGET_URL;
        }
        // check and remove temp token
//        log.info("forward target Url = {}", targetUrl);
        if (targetUrl == null) {
            throw new IllegalArgumentException("invalid token");
        }
        log.info("success! mapping url exist");
        targetUrlMapper.remove(token);

        // get target page
        Integer partitionNo = partitionNoMapper.get(targetUrl);

        // make internal request url
        targetUrl = REPLACE_URL + extractEndpoint(targetUrl);
//        log.info("targetPage request url = {}", targetUrl);

        String targetPage = targetApiConnector.forward(targetUrl).getBody();
        // increase enter count if queue is active
        WaitingStatusDto waitingStatusDto = queues.get(partitionNo);
        if (waitingStatusDto != null) {
            waitingStatusDto.getEnterCnt().incrementAndGet(); // add just before return considering possible error in forwarding
        }
        return targetPage;
    }

    private String parseHtmlPage(String targetUrl, String html) {

        html = html.replace("/_next", endpoint + "/_next");
        html = html.replace("favicon", "waiting/favicon");

        return html;
    }

    private String extractEndpoint(String targetUrl) {
        int startPos = findIndex(targetUrl, '/', 3);
        return targetUrl.substring(startPos);
    }

    private int findIndex(String s, char c, int cnt) {
        int end = s.length();
        for (int i = 0; i < end; i++) {
            if (s.charAt(i) == c && --cnt == 0) {
                return i;
            }
        }
        return -1;
    }

    @Async
    @Scheduled(cron = "* * * * * *") // 매 초
    public void getNext() {
        try {
            if (activePartitions.isEmpty()) {
                return;
            }
            Map<Integer, BatchResDto> response = consumerConnector.getNext(activePartitions); // 대기 완료된 ip 목록을 가져온다.
            Set<Integer> partitionNos = response.keySet();

            for (Integer partitionNo : partitionNos) {
                WaitingStatusDto waitingStatus = queues.get(partitionNo);

                BatchResDto batchRes = response.get(partitionNo);
                waitingStatus.getDoneSet()
                        .addAll(batchRes.getCurDoneList());
                waitingStatus.setCurrentOffset(batchRes.getCurrentOffset());
                cleanUpOutList(waitingStatus); // 대기하다 나간 사람들 중, 대기 만료된 값 삭제
                // 대기 페이지에서 보여지는 totalQueueSize는 '내가 얼마나 기다려야 하는지' 를 의미하는 값이어야 한다.
                waitingStatus.setTotalQueueSize(batchRes.getTotalQueueSize());

                // capture and calculate previous time's enterCnt
                long enterCnt = waitingStatus.getEnterCnt().get();
                waitingStatus.setEnterCntOfLastTime(
                        (int)(enterCnt - waitingStatus.getEnterCntCapture())
                );
                waitingStatus.setEnterCntCapture(enterCnt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 대기 끝난 사람들 삭제
     */
    private void cleanUpOutList(WaitingStatusDto waitingStatus) {
        long currentOffset = waitingStatus.getCurrentOffset();
        waitingStatus.setOutList(
                waitingStatus.getOutList().stream()
                        .filter(i -> i > currentOffset)
                        .collect(Collectors.toList()) // modifiable list
        );
    }

    public ResponseEntity<?> parsing(String address, String scheme) {

        String serverPort = ":3000";

        HttpHeaders headers = new HttpHeaders();

        String url = address.split("/waiting")[0];

        if(address.contains("image")) {

            String[] imageAddressSplit1 = address.split(url);
            String imageAddressSplit1result = imageAddressSplit1[1];

            String[] imageAddressSplit2 = imageAddressSplit1result.split("/_next");
            String imageAddressSplit2result = imageAddressSplit2[0];

            address = address.replace(imageAddressSplit2result, serverPort);

            String[] imageAddressSplit3 = address.split("url=");

            String[] imageAddressSplit4 = imageAddressSplit3[1].split("%2F_next");
            String imageAddress = imageAddressSplit4[0];

            address = address.replace(imageAddress, "");

            String imageUrl = serverUrl + serverPort + "/_next" + address.split("/_next")[1];

            try {
                byte[] imageBytes = getImageBytes(imageUrl);

                headers.setContentType(MediaType.IMAGE_PNG);

                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
//                return ResponseEntity.ok().body(imageBytes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // favicon 일 경우
        if(address.contains("favicon")) {

            String serverURL = serverUrl + "/favicon.ico";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(serverURL, String.class);

            String result = response.getBody().replace("favicon", "waiting/favicon");

            headers.setContentType(new MediaType("image", "x-icon", StandardCharsets.UTF_8));
            return ResponseEntity.ok().headers(headers).body(result);
        }

        String[] addressSplit = address.split("_next");
        String targetUrl = "/_next" + addressSplit[1];

        String[] endPointSplit = address.split(url);
        String[] endPointSplit2 = endPointSplit[1].split("/_next");
        String endPoint = endPointSplit2[0];

        String serverURL = serverUrl + serverPort + targetUrl;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(serverURL, String.class);

        String result = response.getBody().replace("/_next", endPoint + "/_next");

        // css 일 경우
        if(address.contains("css")) {

            headers.setContentType(new MediaType("text", "css", StandardCharsets.UTF_8));
            return ResponseEntity.ok().headers(headers).body(result);
        }

        headers.setContentType(new MediaType("text", "html", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(result);

    }

    public static byte[] getImageBytes(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try {
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
}
