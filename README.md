# Heuron 백앤드 과제

## 기본 사항
- 테스트 데이터베이는 H2 Databse를 사용하였으며 In-Memory 로동작하여 해당 어플리케이션이 동작할때만 사용할 수 있도록 설계하여
  별도의 설치와 실행/종료의 번거로움을 없앴습니다.
- In-Memory 방식으로 어플리케이션 종료시 데이터가 소거됨으로 테스트가 쉽게 해당 프로젝트에 PostMan 설정 파일을 첨부해놨습니다
  (HuroenAPI.postman.setting.json) 해당파일을 포스트맨에 끌어다 붙이면 자동으로 Collection이 생성되어 테스트를 좀더 쉽게 할수 있게 준비했습니다.
- PostMan 설정파일은 README.md와 같은 디렉토리에 있습니다.
- 환자 등록조회 api 사용시 사진은 jpeg, png만 사용하도록 설계했습니다.


## 1. 환자 조회 api
### [GET] /api/v1/patients/{pid}
 - **쿼리 파라미터**
   - pid 환자의 pid
- **Headers:**
  ```json
  {
    "Accept": "application/json"
  }
  ```
- **Response:**
  ```json
    {
      "patientId": 1,               -- 환자의 pid
      "patientName": "홍길동",        -- 환자의 이름  
      "sexCd": "M",                 -- 환자의 성별(M:남자/F:여자) 
      "age": 30,                    -- 환자의 나이
      "diseaseStatus": "Y",         -- 환자의 질병유무(Y/N)
      "patImgUrl": "/api/v1/patients/1/pat-image/patient.png" -- 환자 이미지 조회 URL
    }
  ```
- **성공코드**
  - 200(OK)
- **에러코드**
  - 404(NOT FOUND) : 조회결과 없음
  - 500(INTERNAL SERVER ERROR) : 내부 에러 발생

## 2. 환자 이미지 조회 api
### [GET] /api/v1/patients/1/pat-image/{imageName}
- **쿼리 파라미터**
  - pid : 환자의 pid
  - image : 해당 이미지 이름 
- **Response:**
  ```
    요청시 이미지 로드
  ```
- **성공코드**
  - 200(OK)
- **에러코드**
  - 404(NOT FOUND) : 조회결과 없음
  - 500(INTERNAL SERVER ERROR) : 내부 에러 발생

- **설명**
  - Restful 형식을 준수함과 동시에 정적 자원으로 요청이 나게끔 URL설계를 진행하였습니다.
  - 환자 정보 조회 api를 통해 환자 이미지 조회 url을 바로 사용이 가능한 구조입니다.
     

## 3. 환자 등록 api
### [POST] /api/v1/patients
- **쿼리 파라미터**
    - 없음 
- **Headers(성공)**
  ```json
  {
    "Content-Type": "multipart/form-data"
  }
  ```
- **Request Body(성공시)**
  - 필드명 : patInfoRequest (타입 : application/json)
    ```json
    {
      "patientName": "홍길동",        -- 환자의 이름  
      "sexCd": "M",                 -- 환자의 성별(M:남자/F:여자) 
      "age": 30,                    -- 환자의 나이
      "diseaseStatus": "Y",         -- 환자의 질병유무(Y/N)
    } 
    ```
  - 필드명 : patImg (File)  
- **cURL 예제**
  ```bash
  curl -X POST "http://localhost:8080/api/v1/patients" \
  -H "Content-Type: multipart/form-data" \
  -F "patInfoRequest={\"patientName\":\"홍길동\",\"age\":30,\"sexCd\":\"M\",\"diseaseStatus\":\"Y\"};type=application/json" \
  -F "patImg=@pikachu.png"
  ```
- **성공코드**
  - 200(OK)
- **에러코드**
  - 400(BAD REQUEST) : 잘못된 형식 요청 또는 유효성 검사 실패(JSON 형식 오류, 필수 필드 누락)
  - 415(Unsupported Media Type) : 지원하지 않는 이미지 파일 업로드 (jpeg, png만 가능)
  - 500(INTERNAL SERVER ERROR) : 내부 에러 발생
- **성공 Response:**
  ```json
    {
      "patientId": 1,               -- 환자의 pid
      "patientName": "홍길동",        -- 환자의 이름  
      "sexCd": "M",                 -- 환자의 성별(M:남자/F:여자) 
      "age": 30,                    -- 환자의 나이
      "diseaseStatus": "Y",         -- 환자의 질병유무(Y/N)
      "patImgUrl": "/api/v1/patients/1/pat-image/patient.png" -- 환자 이미지 경로
    }
  ```
- **실패 Response (유효성 검사에서 실패한경우)**
  ```json
  {
    "message": "Validation failed for one or more fields",
    "errors": {
      "age": "나이는 0세 이상 이어야 합니다"
    },
    "status": 400
  }   
  ```
- **설명**
  - 이미지와 환자정보를 한번에 등록하기 위해 multipart/form-data 전송을 진행했습니다.
  - 이미지가 완벽하게 업로드가 되기 전까지는 환자 조회가 불가능하게 구현하였습니다.
  - 잘못된 환자정보(나이가 음수인경우, 필수값이 없는 경우)에 대한 유효성검사를 추가했으며 실패시 400번 응답코드와
    클라이언트가 빠르게 확인할 수 있게 body에 에러 메시지를 넣어주었습니다.
  - 파일은 해당 프로잭트의 상단에 image 폴더가 생기며 검색속도의 개선을 위해 pid 별로 폴더를 만들어 저장하였습니다.
    이 같은 설계를 한 이유로 pid 별로 폴더를 만들고 저장할 경우에 추후에 이미지를 추가해야 할 상황이 생길때 pid 폴더
    단위로 저장할 경우 검색속도를 어느정도 보장 할 수 있기 때문에 이같은 설계로 진행하였습니다.


## 4. 환자 정보 삭제 API
### [DELETE] /api/v1/patients/1
- **쿼리 파라미터**
  - pid : 환자의 pid
- **Response:**
  ```
    없음
  ```
- **성공코드**
  - 204(OK, NO CONTENT)
- **에러코드**
  - 404(NOT FOUND) : 삭제할 환자 정보가 없음
  - 500(INTERNAL SERVER ERROR) : 삭제 진행시 내부 에러 발생

- **설명**
  - OCS 개발경험으로 환자 정보는 삭제가 되면 안되는 원칙을 준수할 수 있게 삭제 필드를 따로 구현하여 환자 정보 삭제 요청시 해당 필드값을 RMV_YN 값을 Y로 변경하며
    해당 삭제 날짜를 등록(LAST_UDDT 컬럼)하여 삭제 일시를 파악하도록 하였습니다.
  - 환자 정보 삭제 시 환자의 이미지도 환자 정보에 해당함으로 삭제하지 않고 별도의 폴더를 만들고 (image_delete) 해당 폴더에 환자정보이미지를
    옮겨놓도록 구현하여 환자 정보를 보호함과 동시에 환자 검색시 빠르게 찾을 수 있도록 구현하였습니다.