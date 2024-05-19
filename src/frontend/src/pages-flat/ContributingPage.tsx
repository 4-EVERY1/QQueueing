import { SectionTitle } from "@/shared";
import React from "react";

const ContributingPage = () => {
  return (
    <div className="flex flex-col flex-1 gap-2 bg-white rounded-md border">
      <SectionTitle title="Contribution" />
      <div className="p-3 text-[1.2rem]">
        <h1 className="text-[1.2rem] font-bold">코드 기여 지침</h1>
        <p>
          QQueueing은 무료이고 오픈된 프로젝트이며 우리는 커뮤니티에서의 기여를
          환영합니다! 여러분의 참여 방법은 다양합니다. 튜토리얼이나 블로그 글
          작성, 문서 개선, 버그 리포트 및 기능 제안 또는 QQueueing 자체에 통합될
          수 있는 코드 작성 등이 있습니다.
        </p>
        &nbsp;
        <h2 className="font-bold">버그 리포트</h2>
        <p>
          QQueueing에서 버그를 발견한 것으로 생각된다면, 먼저 최신 버전의
          QQueueing을 테스트하는지 확인하세요. 문제가 이미 수정되었을 수도
          있습니다. 그렇지 않은 경우, 유사한 문제가 이미 열려있는지 GitLab의{" "}
          <a href="https://lab.ssafy.com/s10-final/S10P31A401/-/issues">
            이슈 목록
          </a>
          에서 확인하세요.
        </p>
        &nbsp;
        <h2 className="font-bold">기능 제안</h2>
        <p>
          QQueueing에 없는 기능을 원한다면, 여러분이 원하는 기능이 다른
          사람들에게도 필요할 수 있습니다. QQueueing이 현재 갖고 있는 많은
          기능들은 사용자들이 필요성을 느껴 추가된 것입니다. GitLab의{" "}
          <a href="https://lab.ssafy.com/s10-final/S10P31A401/-/issues">
            이슈 목록
          </a>
          에서 기능을 설명하고, 왜 필요한지 그리고 어떻게 작동해야 하는지
          설명하는 이슈를 열어주세요.
        </p>
        &nbsp;
        <h2 className="font-bold">코드 및 문서 변경 사항 기여</h2>
        <p>
          새로운 기능이나 버그 수정을 QQueueing에 기여하고 싶다면, 먼저 GitLab
          이슈에서 여러분의 아이디어를 논의해주세요. 여러분의 아이디어에 대한
          GitLab 이슈가 없는 경우, 새로 열어주세요. 이미 누군가 작업 중이거나
          시작하기 전에 알아야 할 특별한 복잡성이 있을 수 있습니다. 문제를
          해결하는 여러 가지 방법이 있으며, 병합할 수 없는 PR에 시간을 낭비하기
          전에 올바른 접근 방식을 찾는 것이 중요합니다. 우리는 커뮤니티 기여가
          특히 환영되는 기존 GitLab 이슈에 &quot;help wanted&quot; 라벨을
          추가하고, 새로운 기여자에게 적합한 이슈에 &quot;good first issue&quot;
          라벨을 사용합니다.
        </p>
        &nbsp;
        <p>
          <a href="https://lab.ssafy.com/s10-final/S10P31A401">
            QQueueing 저장소
          </a>
          에 대한 기여 절차는 유사합니다. 개별 프로젝트에 대한 자세한 내용은
          아래에서 찾을 수 있습니다.
        </p>
        &nbsp;
        <h3 className="font-bold">저장소를 포크하고 복제합니다.</h3>
        <p>
          마스터 QQueueing 코드 또는 문서 저장소를 포크하고 로컬 머신에 복제해야
          합니다. 도움이 필요하면{" "}
          <a href="https://docs.gitlab.com/ee/user/project/repository/forking_workflow.html">
            GitLab 도움말 페이지
          </a>
          를 참조하세요. 개별 프로젝트에 대한 추가 지침은 아래에서 제공됩니다.
        </p>
        &nbsp;
        <h3 className="font-bold">코드 변경 사항에 대한 팁</h3>
        <p>
          풀 리퀘스트를 올리기 전에 이 팁을 따르면 리뷰 사이클을 빠르게 할 수
          있습니다.
        </p>
        &nbsp;
        <ul className="list-disc ml-10">
          <li>적절한 유닛 테스트 추가</li>
          <li>적용 가능한 경우 통합 테스트 추가</li>
          <li>
            변경되지 않은 줄은 편집하지 않아야 합니다(예: 변경되지 않은 줄 서식
            지정, 기존 임포트 재정렬 금지)
          </li>
          <li>
            새로운 파일에 적절한{" "}
            <a href="https://lab.ssafy.com/s10-final/S10P31A401/-/blob/dev/LICENSE?ref_type=heads">
              라이선스 헤더
            </a>{" "}
            추가
          </li>
        </ul>
        &nbsp;
        <h3 className="font-bold">변경 사항 제출</h3>
        <p>변경 사항과 테스트가 준비되었다면 다음을 제출하세요.</p>
        <ol className="list-disc ml-10">
          <li>
            <p>변경 사항을 테스트하세요</p>
            <p> 테스트 스위트를 실행하여 아무 문제가 없는지 확인하세요.</p>
          </li>
          <li>
            <p>변경 사항을 리베이스하세요</p>
            <p>
              로컬 저장소를 최신 QQueueing 저장소 코드로 업데이트하고, 브랜치를
              최신 마스터 브랜치 위에 리베이스하세요. 우리는 초기 변경 사항을
              하나의 커밋으로 압축하는 것을 선호합니다. 나중에 변경 사항을 요청
              받으면 별도의 커밋으로 추가하세요. 이렇게 하면 리뷰가 쉬워집니다.
              병합하기 전에 최종적으로 모든 커밋을 스쿼시하거나 리뷰어가 요청할
              때 우리가 대신 해줍니다.
            </p>
          </li>
          <li>
            <p>풀 리퀘스트를 제출하세요</p>
            <p>
              로컬 변경 사항을 포크한 저장소에 푸시하고 병합 요청을 제출하세요.
              풀 리퀘스트에서는 여러분이 한 변경 사항을 요약하는 제목을
              선택하고, 변경 사항에 대한 자세한 내용을 본문에 제공하세요. 또한
              토론이 진행된 이슈 번호를 언급하세요. 예: &quot;Closes #123&quot;.
            </p>
          </li>
        </ol>
        &nbsp;
        <p>
          그런 다음 기다리세요. 풀 리퀘스트에 대한 토론이 있을 것이며, 필요한
          경우 변경 사항을 함께 작업하여 여러분의 풀 리퀘스트를 QQueueing에
          병합할 것입니다. yaml 변경 로그 항목이 자동으로 생성됩니다. 외부
          기여자가 수동으로 편집할 필요가 없습니다. 리뷰어가 리베이스를 요청할
          수 있지만, 일반적으로 마지막에 스쿼시하는 것은 하지 않아야 합니다.
          이것은 풀 리퀘스트가 GitLab을 통해 통합될 때 수행할 수 있습니다.
        </p>
        &nbsp;
        <p>
          공개적으로 공유된 브랜치에 강제 푸시하지 않는 일반 지침을 준수하세요.
          풀 리퀘스트를 열면 브랜치가 공개적으로 공유된 것으로 간주됩니다. 강제
          푸시 대신 점진적인 커밋을 추가하세요. 이것이 리뷰어에게 일반적으로 더
          쉽습니다. 마스터에서 변경 사항을 가져와야 하는 경우 마스터를 브랜치로
          병합할 수 있습니다. 리뷰어가 긴 기간 동안 진행 중인 풀 리퀘스트에
          리베이스하라고 요청할 수 있으며, 그 경우 강제 푸시하는 것은
          괜찮습니다. 주의할 점은 리뷰 프로세스가 끝날 때 스쿼시하지 않아야
          한다는 것입니다. 이것은 풀 리퀘스트가{" "}
          <a href="https://docs.gitlab.com/ee/user/project/merge_requests/squash_and_merge.html">
            GitLab을 통해 통합
          </a>
          될 때 수행할 수 있습니다.
        </p>
        &nbsp;
      </div>
    </div>
  );
};

export default ContributingPage;
