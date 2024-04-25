package com.qqueueing.main;

import com.qqueueing.main.connect.ConsumerConnector;
import com.qqueueing.main.connect.ProducerConnector;
import com.qqueueing.main.model.GetMyOrderResDto;
import com.qqueueing.main.model.TestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


//@Slf4j
@RequestMapping("/waiting")
@RestController
//@RequiredArgsConstructor
public class WaitingController {

    private final WaitingService waitingService;

    public WaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }


    @PostMapping
    public ResponseEntity<?> enter(HttpServletRequest request) {
        // 요청이 프록시 등을 거쳐 넘어왔을 때, 원래 클라이언트의 ip주소를 담는 헤더
//        String ip = request.getHeader("X-Forwarded-For");
//        if (ip == null) {
//            ip = request.getRemoteAddr(); // 프록시 안 거쳤을 때
//        }
        try {

            return ResponseEntity
                    .ok(waitingService.enter("tmpUserId"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e);
        }
    }

    @PostMapping("/{waitingIdx}")
    public ResponseEntity<GetMyOrderResDto> getMyOrder(@PathVariable Long waitingIdx,
                                                       @RequestBody String idVal) {
        System.out.println("idVal = " + idVal);
//        GetMyOrderResDto myOrder = waitingService.getMyOrder(waitingIdx, idVal);
        return ResponseEntity
                .ok(waitingService.getMyOrder(waitingIdx, idVal));
//        if (myOrder.getTotalQueueSize() == -1) {
//        }
    }

    @GetMapping("/{waitingIdx}/out")
    public ResponseEntity<Void> out(@PathVariable Long waitingIdx) {
        waitingService.out(waitingIdx);
        return ResponseEntity
                .ok()
                .build();
    }
}
