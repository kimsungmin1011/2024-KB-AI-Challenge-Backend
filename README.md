# 🛡️KB AI Sentry
부실 기업 조기경보를 위한 AI 기반 재무 리스크 분석 시스템

![kb ai sentry 구조도](https://github.com/user-attachments/assets/6423aa08-2671-43eb-9371-f04d2b736945)

---

## 프로젝트 개요

**KB AI Sentry**는 기업의 재무 데이터를 실시간 분석하여 **부실 가능성을 조기에 탐지**하는 인공지능 기반 시스템입니다.  
DART에서 수집한 데이터를 분석하고, GPT 기반 LLM이 재무 상태를 평가합니다.  
**태영건설, 티몬/위메프 사태**와 같은 사례를 사전에 방지하기 위해 개발하였습니다.

---

## 프로젝트 목표

- 금융감독원 등록 기업 10만 개 이상을 **AI로 자동 분석**
- 재무제표 및 주요사항보고서를 기반으로 **위험도 예측**
- **조기경보 시스템**으로 기업 위험을 등급화 (A~F)
- **OPEN DART API** 연동을 통해 실시간 데이터 수집 및 분석

---

## 프롬프트 엔지니어링 (RAG 기반)

본 시스템은 GPT 기반 LLM을 통해 재무 리스크 평가를 수행하며, **RAG (Retrieval-Augmented Generation)** 아키텍처를 적용했습니다.

- **프롬프트 엔지니어링 포인트**
    - 사전 정의된 재무 지표를 기반으로 마크다운 테이블 생성
    - 분기별 재무 데이터를 비교 분석
    - 위험 등급을 A~F로 평가
    - 데이터 부족 시 *"데이터가 부족하여 답변을 제공할 수 없습니다."* 출력
    - **할루시네이션 확률 46% → 2% 감소 (100회 호출 기준)**

<details>
<summary>📄 프롬프트 보기</summary>

```
"You are a financial statement analysis expert specializing in professional risk assessments for companies. "
"Below is the financial data for a company, including key indicators and major events. "
"Based on this data, assign a risk grade from A (lowest risk) to F (very high risk) and provide a final opinion on the company's financial condition.\n\n"

"Context: The data includes financial indicators such as profitability, stability metrics, and significant events that may impact the company's risk level."
"Task: "1. Start your response with the risk grade in large font."
"2. Provide a detailed final opinion on the company's financial condition, considering the major events."
"3. If any data is missing or cannot be interpreted, clearly state that you cannot provide an answer. "
"Do not create or hallucinate any data. "
"If data is insufficient, clearly state in Korean that you cannot provide an answer due to lack of data."

"Response format:"
"1. Risk grade (in large font, in Korean)."
"2. Final opinion (in markdown format, in Korean)."
"Here is the data:"

Task:  
1. 요약: 주요 위험 요소를 간단하게 분석합니다.  
2. 테이블: 분기별 주요 지표를 비교 분석합니다.  
3. 등급: 위험 수준을 A (최저) ~ F (최고) 등급으로 평가합니다.

데이터가 부족하면 한국어로 명확히 "데이터가 부족하여 답변을 제공할 수 없습니다."라고 작성하세요.
```

</details>

---
## 🗂️ 시스템 구성 상세 설명

### 주요 컴포넌트

- **사용자 진입점**
    - `ziggle.kr` (DNS 도메인)
    - 사용자의 모든 요청은 `443` 포트를 통해 **Nginx 리버스 프록시**로 유입됨


- **리버스 프록시 서버**
    - `Nginx` (내부 IP: 192.168.2.35)
    - 외부 요청을 내부 서비스로 라우팅
        - `/api` → **Spring Cloud Gateway** (포트 10000)
        - `/web/kb-ai-challenge` → **프론트엔드 웹 서버** (포트 20401)


- **프론트엔드**
    - `msa-kb-ai-challenge-web` (포트 20401)
    - 사용자 인터페이스 및 화면 제공


- **백엔드 MSA**
    - `msa-kb-ai-challenge-api` (포트 30901)
        - 기업 재무 분석 API
        - **OpenAI API**, **Open DART API** 연동
    - `msa-log-api` (포트 10103)
        - 사용자 로그 및 요청 기록 관리


- **API Gateway**
    - **Spring Cloud Gateway** (포트 10000)
    - 내부 MSA API 라우팅
        - `/api/kb-ai-challenge`, `/api/log` 등


- **데이터베이스**
    - **MariaDB** (포트 33066)
    - 모든 재무 정보 및 로그 영속 저장


- **CI/CD 파이프라인**
    - **Jenkins** (포트 9001)
        - GitHub Webhook 연동
        - 코드 푸시 → 자동 빌드 및 배포
    - **GitHub 저장소**


- **모니터링**
    - **Grafana** (포트 9002)
    - 대시보드 기반 실시간 상태 시각화
---

## 주요 기능

- 기업 검색 및 기본 개황 제공
- 분기별 재무 비율 분석
- 종합 위험 의견 자동 생성 (GPT)
- 등급 시스템 (A~F) 적용
- 실시간 데이터 반영

---

## 주요 분석 지표

| 지표명 | 설명 |
|--------|------|
| 세전계속사업이익률 | 본업 수익성 (EBIT / 매출) |
| 순이익률 | 순이익 / 매출 |
| 총포괄이익률 | 기타포괄손익 포함 수익성 |
| 매출총이익률 | 매출 – 원가의 비율 |
| 매출원가율 | 수익 대비 원가 비율 |
| ROE | 자기자본 이익률 |
| 판관비율 | 비용 통제력 지표 |
| 총자산영업이익률 | 자산 대비 수익 창출 효율 |
| 자기자본영업이익률 | 자기자본 대비 본업 수익성 |

---

## 실행 화면 예시

| 홈 화면                                                                                            | 재무 분석 화면                                                                                           | 종합 의견 화면 |
|-------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------|----------------|
| ![kb 홈화면](https://github.com/user-attachments/assets/b23f0bf2-04b3-4dfe-865b-2828932a9918) | ![kb 재무분석](https://github.com/user-attachments/assets/9b75e9aa-af29-4c46-8e7e-b36e25ef8fc6) | ![kb 종합의견](https://github.com/user-attachments/assets/0854160a-21e6-487d-a8d1-233177dbd098) |

---

## 팀 소개

- **팀장**: 김성민
- **팀원**: 전민수, 김주희

---

## 🌐 데모 링크

[https://ziggle.kr/web/kb-ai-challenge/pages/home](https://ziggle.kr/web/kb-ai-challenge/pages/home)

---

## 라이선스

본 프로젝트는 비영리 목적의 AI 챌린지를 위한 데모로, 저작권은 KB AI Sentry 개발팀에게 있습니다.
