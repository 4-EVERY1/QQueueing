<br>

![QQueueing](./.asset/queueing.png)  

<br>

<div align="left">
    <h1>QQueueing</h1>
</div>

QQueueing은 서버에 직접 설치해서 사용할 수 있는 무료 대기열 서비스입니다. 서버 프로그램이 실행 중인 곳에 설치해서, 추가 비용 없이 대기열 기능을 사용할 수 있습니다.

<br>

## Features

- 소스코드 변경 없이 다운로드를 통해 적용 가능한 대기열 기능
  - [대기열 기능 자세히 보기](https://lab.ssafy.com/s10-final/S10P31A401/-/wikis/features/queueing)
- 대기열 타깃 url 설정 및 활성/비활성화 기능 제공
- 타깃 url 별로 대기 및 통과 인원 현황 모니터링 화면 제공
- 운영자의 컴퓨팅 자원과 대기열 어플리케이션의 모니터링 지표 제공
  - [모니터링 기능 자세히 보기](https://lab.ssafy.com/s10-final/S10P31A401/-/wikis/features/monitoring)
- 모바일, PC 호환 지원


<br>

## Getting Started
#### 구동 환경
 - 도커, 도커 컴포즈(2.17이상), 파이썬3(3.8이상)
 - nginx 리버스 프록시 서버를 띄우고 있는 상황에서 동작합니다.
- nginx 서버는 컨테이너 런타임을 통해 실행되고 있어야 합니다.
- 기본 웹 통신인 80, 443 포트를 사용하고 있는 상황에서 동작합니다.
- 추후 개발을 통해 확장성을 갖출 예정입니다.

## Terminal Settings
1. 레포지토리를 클론 받은 후, 해당 레포지토리로 이동합니다.
```sh
git clone https://lab.ssafy.com/s10-final/S10P31A401.git qqueueing
cd qqueueing
```
2. `qqueueing.sh`를 통해 서비스를 조작할 수 있습니다.   
![command](./.asset/qqu-command.png)      
3. `install`을 통해 대기열 시스템 초기 설정을 진행합니다.
```sh
./qqueueing.sh install
```
  - 이때 입력된 사항은 .env 파일로 저장되며, 이 파일을 직접적으로 수정할 수도 있습니다.
  - .env 파일을 수정하면 반드시 다시 install을 진행해주십시오.  
4. 설정이 완료되면 `start`를 통해 애플리케이션을 실행합니다.
```sh
./qqueueing.sh start
```
- .env 파일에 명시된 포트(기본 3001)로 어드민 페이지에 접근하여 GUI를 통한 설정을 진행할 수 있습니다.
5. 대기열 시스템을 중지하고 싶을 때는 `stop`을 입력합니다.
```sh
./qqueueing.sh stop
```

<br>

## User's Guide

<b>대기열 애플리케이션을 동작시킵니다.</b>

<img src="./.asset/대기열_첫_화면.PNG" width="1000" height="500">
<br><br><br><br>

<b>등록하기 버튼을 누르고, 대기열을 적용할 URL, 서비스 명, 대기열 대표 이미지를 등록합니다.</b>

<img src="./.asset/대기열_등록_화면.PNG" width="1000" height="500">
<br><br><br><br>

<b>대기열 리스트 버튼을 누르고, 대기열이 적용된 모습을 확인합니다.</b>

<img src="./.asset/대기열_리스트_화면.PNG" width="1000" height="500">
<br><br><br><br>

<b>등록된 url을 클릭하여 상세 정보를 확인할 수 있고, 활성/비활성화 및 설정을 변경할 수 있습니다. </b>

<img src="./.asset/url_상세_정보_화면.PNG" width="1000" height="500">
<br><br><br><br>

<b>대시보드 버튼을 클릭하여 대기열이 적용된 운영자의 컴퓨팅 자원과, 대기열 어플리케이션의 상태를 모니터링할 수 있습니다.</b>

<img src="./.asset/대쉬보드_gif.gif" width="800" height="400">
<br>
<img src="./.asset/사용자_컴퓨팅_자원_모니터링_gif.gif" width="800" height="400">

<img src="./.asset/대기열_애플리케이션_모니터링_gif.gif" width="800" height="400">

<br>

## Run Screen

- PC Version
<img src="./.asset/pc_대기열_gif.gif" width="600" height="300">
<br><br><br><br>

- Mobile Version
<img src="./.asset/모바일_대기열_gif.gif" width="400" height="500">

<br>

## 👩 Contributers

| [지인성](https://github.com/JIINSUNG)                                                     | [이상학](https://github.com/yee950419)                                                             | [손영훈](https://github.com/syhuni)                                                       | [신문영](https://github.com/ztrl)                                                         | [김동건](https://github.com/Zerotay)                                                      | [강이규](https://github.com/EhighG)                                                              |
| ----------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| <img src="https://avatars.githubusercontent.com/u/49591292?v=4" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/u/65946607?v=4" width="150" height="150">          | <img src="https://avatars.githubusercontent.com/u/74291750?v=4" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/u/88647858?v=4" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/u/67823010?v=4" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/u/71206505?v=4" width="150" height="150">        |

<br>

## Contributing

[CONTRIBUTING](./CONTRIBUTING_KOR.md)에서 코드 기여에 관한 가이던스를 확인하십시오.  
See the [CONTRIBUTING](./CONTRIBUTING.md) for Code Contribution Guidelines.

<br>

## License

[LICENSE](./LICENSE)에서 라이센스 저작권과 제한사항을 확인하십시오.  
See the [LICENSE](./LICENSE) for license rights and limitations.
