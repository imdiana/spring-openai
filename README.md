# Spring-openai

인프런 - 나도! 스프링으로 인공지능을 할 수 있다 (인프1탄)을 시청하고 구현해보기

## 1. Chat Client API

OpenAI의 Chat Client API는 **동기(Synchronous) 방식**과 **스트리밍(Streaming) 방식**을 지원합니다.  
기본적인 동작 흐름은 다음과 같습니다

1. `prompt()` - **프롬프트 생성**
2. `user(message)` - **사용자 메시지 입력**
3. `call()` - **요청 실행**
4. `content()` - **응답 정보 반환**

이 과정을 통해 `GPT-4o`를 활용하여 ChatGPT 응답을 요청하고 처리할 수 있습니다.


---

## 2. AI 모델의 다양한 역할 (프롬프트 구조)

### 2.1 구성 요소

- **System Role**: AI가 수행할 수 있는 다양한 역할
- **System Message Prompt**: AI의 역할을 정의하는 메시지
- **User Message Prompt**: 사용자가 AI에게 요청할 실제 작업

이러한 **System Message**와 **User Message**를 조합하여 원하는 결과를 도출하는 기술을  
**프롬프트 엔지니어링(Prompt Engineering)**이라고 합니다.


---

## 3. Placeholder

프롬프트 내에서 동적으로 값을 설정할 수 있도록 **Placeholder**를 사용합니다.  
예제:

```kotlin
chatClient.prompt()
    .user("메시지 내용")
    .system { it.param("subject", "주제").param("tone", "응답 스타일") }
    .call()
    .content()
```

- `subject` → 대화의 **주제**
- `tone` → 응답의 **표현 방식**

이처럼 프롬프트를 정의하고, 필요한 요소를 request로 받아 적용하여 **동적인 응답을 생성**할 수 있습니다.


---

## 4. ChatResponse

### 4.1 응답 JSON 구조

다음은 `ChatResponse`의 기본적인 JSON 응답 형태입니다.

```json
{
  "results": [
    {
      "metadata": {
        "finishReason": "STOP",
        "contentFilters": [],
        "empty": true
      },
      "output": {
        "messageType": "ASSISTANT",
        "metadata": {
          "finishReason": "STOP",
          "id": "chatcmpl-B6~~~",
          "role": "ASSISTANT"
        },
        "text": "미국의 수도는 워싱턴 D.C.입니다."
      }
    }
  ],
  "metadata": {
    "model": "gpt-4o-mini-2024-07-18",
    "usage": {
      "promptTokens": 13,
      "completionTokens": 14,
      "totalTokens": 27
    }
  }
}

```

중요한 필드

- `results.output.text` → AI의 응답 내용
- `metadata.model` → 사용된 AI 모델 (`gpt-4o-mini-2024-07-18`)
- `usage.totalTokens` → 요청에서 사용된 총 토큰 수

--- 

## 5. Entity 형태로 응답 받기

ChatClient를 통해 응답을 받을 때, 원하는 형식으로 **반환 타입**을 지정할 수 있습니다.

### 5.1 Object 형태 변환

객체로 변환하여 응답을 받을 수 있습니다.

```kotlin
chatClient.prompt()
    .user("메시지 내용")
    .call()
    .entity(Answer::class.java)  // Answer 객체로 변환
```

### 5.2 구조화된 데이터 출력

Spring의 `ListOutputConverter`를 활용하여 리스트 형태로 변환할 수 있습니다.

```kotlin
chatClient.prompt()
    .user("메시지 내용")
    .call()
    .entity(ListOutputConverter(DefaultConversionService()))  // 리스트 변환
```

### 5.3 Map 형태로 변환

`ParameterizedTypeReference`를 사용하여 `Map<String, String>` 형태로 변환할 수 있습니다.

```kotlin
chatClient.prompt()
    .user("메시지 내용")
    .call()
    .entity(object : ParameterizedTypeReference<Map<String, String>>() {})
```

### 5.4 List 형태로 변환

`ParameterizedTypeReference`를 사용하여 `List<Movie>` 형태로 변환할 수 있습니다.

```kotlin
chatClient.prompt()
    .user("메시지 내용")
    .call()
    .entity(object : ParameterizedTypeReference<List<Movie>>() {})
```

--- 

## 6. Advisor

ChatClient에는 기본적으로 **대화 히스토리를 기억하고 활용할 수 있는 Advisor**가 포함되어 있습니다.

### 6.1 ChatClient 기본 Advisor

#### 6.1.1 MessageChatMemoryAdvisor

- `InMemoryChatMemory`를 활용하여 이전 대화 내용을 저장하고, 이를 기반으로 **더욱 자연스러운 응답을 생성**할 수 있습니다.
- 예를 들어, 사용자가 특정한 맥락에서 질문을 하면, AI는 이전 대화를 참조하여 연속된 문맥을 유지하는 답변을 할 수 있습니다.

--- 

## 7. OpenAiChatOptions

`OpenAiChatOptions`는 Chat 모델과 관련된 다양한 설정을 구성할 수 있다.

- **model**: 사용할 AI 모델 (예: gpt-4, gpt-4o)
- **temperature**: 응답의 창의성 조절 (낮을수록 보수적, 높을수록 창의적)
- **frequency_penalty**: 반복되는 단어 감소 (높을수록 중복 표현 감소)
- **presence_penalty**: 새로운 주제 도입 유도

```kotlin
chatModel.call(
    object : Prompt(
        message,
        OpenAiChatOptions.builder()
            .model("gpt-4o")
            .temperature(0.4)
        .build()
    ) {}
)
```

---

## 8. Image Generation - DALL·E 3

DALL·E 3을 활용한 이미지 생성 시 프롬프트 구성 요소

- **주제**: 이미지가 표현할 주요 개념
- **시각적 요소**: 등장 인물, 사물, 배경 요소
- **상세 설명**: 색상, 조명, 분위기 등 구체적인 디테일
- **순서와 구조**: 배치 및 구도 설정
- **스타일과 텍스처**: 그림체, 질감, 화풍 등
- **기타 특이사항**: 추가적인 조건 (예: "HDR 효과 적용", "애니메이션 스타일")

```json
{
  "imageResponseMetadata": "...",
  "imageGenerations": [
    {
      "imageGenerationMetadata": {
        "revisedPrompt": "Visualize a serene and secluded house located in Bali...",
        "image": {
          "url": "https://oaidalleapiprodscus.blob.core.windows.net/.../img-9ruZMQGmUWhl9x4iKxty1VVZ.png",
          "b64Json": null
        }
      }
    }
  ]
}
```

---

## 9. Web Speech & OpenAI로 음성 기반 이미지 생성

브라우저의 Web Speech API를 활용하여 음성을 인식하고, 이를 OpenAI에 전달하여 이미지 생성 가능

### 음성 인식 API

- window.SpeechRecognition: 최신 브라우저(Chrome, Edge)에서 지원
- window.webkitSpeechRecognition: 구버전 브라우저에서 지원
- event.results[0][0].transcript: 음성을 텍스트로 변환

```javascript
const recognition = new window.SpeechRecognition();
recognition.onresult = (event) => {
  const transcript = event.results[0][0].transcript;
  console.log("사용자 음성:", transcript);
};
recognition.start();
```

#### OpenAI 응답 예시 (1980년대 서울)

```json
{
  "imageResponseMetadata": "...",
  "imageGenerations": [
    {
      "imageGenerationMetadata": {
        "revisedPrompt": "An expansive view of Seoul, South Korea during the 1980s...",
        "image": {
          "url": "https://oaidalleapiprodscus.blob.core.windows.net/.../img-Ykvh0yVhXoF8eC6nDOmpzdrg.png",
          "b64Json": null
        }
      }
    }
  ]
}
```

### OpenAI 이미지 생성 정책 및 오류 대응

`DALL·E 3`를 통한 이미지 생성 시 `안전 정책(Content Policy) 위반`으로 인해 요청이 거부될 수 있음.

```json
{
  "error": {
    "code": "content_policy_violation",
    "message": "Your request was rejected as a result of our safety system.",
    "type": "invalid_request_error"
  }
}
```

#### 오류 해결 방법

- **프롬프트 수정**: 민감한 키워드 제거 (예: "사람 얼굴", "유명인")
- **대체 표현 사용**: "애니메이션 스타일의 캐릭터" 등으로 변경
- **OpenAI 정책 확인**: OpenAI Content Policy 참고

---

