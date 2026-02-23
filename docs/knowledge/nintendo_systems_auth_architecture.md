# ニンテンドーアカウント リノベーションプロジェクトと認証基盤

出典: AWS Summit Tokyo 2023 / Nintendo Systems

## プロジェクト概要

ニンテンドーアカウント（2015年リリース、164カ国・2.9億アカウント以上）の実装基盤を刷新するプロジェクト。

- **移行前**: EC2 + Perl
- **移行後**: ECS on AWS Fargate + Java
- **インフラ管理**: AWS CDK (TypeScript)
- **目標**: 継続性強化・無停止移行・新規開発継続

## 重要な前提：ニンテンドーアカウント自体が認証基盤

ニンテンドーアカウントは OAuth 2.0 / OpenID Connect 準拠の **Authorization Server を自社実装** している。
Keycloak のような OSS を使うのではなく、認証基盤そのものを開発・運用している点が特徴。

```
Nintendo Switch / スマホアプリ
        ↓
ニンテンドーアカウント（OAuth 2.0 / OIDC 認証基盤）
  └── Authorization Server（Perl → Java へ移行中）
  └── Resource Server 群（約10サービス）
        ↓
各種サービス（eShop, NSO, etc.）
```

## このスケールで認証基盤に求められること

| 関心事 | 内容 |
|--------|------|
| スケール | 2.9億アカウント、164カ国 → 高可用性・水平スケール必須 |
| マルチ環境 | 約20環境 → AWS CDK で Infrastructure as Code |
| ゼロダウンタイム移行 | 無停止で Perl → Java へ切り替え（Strangler Fig パターン） |
| マイクロサービス間の信頼 | 約10サービス → サービス間の認可（内部JWT or mTLS） |
| セッション管理 | ElastiCache（Redis/Memcached）でトークン・セッションを共有 |
| 非同期処理 | SQS + Step Functions → メール送信・アカウント削除などの副作用を非同期化 |

## AWSサービス構成

- **コンピューティング**: ECS on AWS Fargate
- **DB**: Amazon Aurora
- **キャッシュ**: ElastiCache (Redis / Memcached)
- **メッセージ**: Amazon SQS
- **ワークフロー**: AWS Step Functions
- **シークレット管理**: AWS Secrets Manager
- **監視**: Amazon CloudWatch + SNS

## quarkus-todo での学習との接点

| quarkus-todo で学ぶこと | 実務での位置づけ |
|------------------------|----------------|
| Keycloak (Authorization Server) | ニンテンドーアカウント本体 |
| quarkus-oidc (Resource Server) | eShop 等の各サービス側 |
| JWT 検証・ロール判定 | サービス間の認可ロジック |

quarkus-todo + Keycloak の学習は「Resource Server 側の実装パターン」を習得すること。
本番では自社実装の Authorization Server になるが、トークンの構造・検証・スコープの概念は同じ。

## このプロジェクトに関わるために重要な知識

1. **OAuth 2.0 / OIDC の仕様レベルの理解**
   - RFC 6749 (OAuth 2.0), RFC 7519 (JWT), OpenID Connect Core
   - OSS を「使う側」でなく、Authorization Server を「実装・運用する側」の視点

2. **トークンのライフサイクル管理**
   - アクセストークン（短命）/ リフレッシュトークン（長命）の発行・検証・失効
   - Redis を使ったトークンの分散管理

3. **マイクロサービスにおける認証伝播**
   - API Gateway でトークン検証 → 後段サービスへは検証済みユーザー情報を渡す

4. **ゼロダウンタイム移行パターン**
   - Strangler Fig パターン: 旧実装と新実装を並走させ、段階的に切り替える
