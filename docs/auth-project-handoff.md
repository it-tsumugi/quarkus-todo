# 認証基盤学習プロジェクト 引き継ぎドキュメント

## なぜこのプロジェクトを作るのか

### 背景
認証システム部署への異動を見据えて、OAuth 2.0 / OIDC の実装レベルの知識を習得する。

### 直接のきっかけ
ニンテンドーアカウント リノベーションプロジェクト（AWS Summit Tokyo 2023）への関与予定。
- ニンテンドーアカウントは OAuth 2.0 / OIDC 準拠の Authorization Server を **自社実装** している
- 既存: EC2 + Perl → 移行先: ECS on AWS Fargate + Java
- 規模: 164カ国・2.9億アカウント以上、約10マイクロサービス、約20環境

このプロジェクトで必要なのは「Keycloak を使う側」ではなく、**Authorization Server を実装・運用する側** の知識。

---

## quarkus-todo で習得済みの前提知識

- Quarkus（RESTEasy Reactive、Panache ORM、Dev Services）
- PostgreSQL + JPA によるCRUD実装
- Docker / Docker Compose
- GitHub Actions による CI（JVM テスト + フロントエンドビルド）
- 認証基盤の概念（認証・認可・JWT・OAuth2・SSO の基礎）

---

## このプロジェクトの目的

Authorization Server を「使う側」から「作る側」へ理解を深める。

| フェーズ | 内容 |
|--------|------|
| 1 | Keycloak を動かし、OAuth 2.0 / OIDC の各フローを実際のリクエスト・レスポンスで理解する |
| 2 | Quarkus で Resource Server（JWT検証・認可）を実装し、トークンを受け取る側を体験する |
| 3 | Authorization Server の内部構造を理解する（トークン発行・管理・失効の仕組み） |
| 4 | 自前の簡易 Authorization Server を実装する（Spring Authorization Server or 自作） |

---

## 学習ロードマップ

### フェーズ 1: Keycloak でプロトコルを理解する

Docker で Keycloak を起動し、以下の Grant Type を curl で実際に試す。

- Authorization Code Flow（PKCE あり）← Web アプリの標準
- Client Credentials Flow ← サービス間認証
- Refresh Token の取得と利用

確認すること:
- `/.well-known/openid-configuration`（Discovery エンドポイント）
- `/protocol/openid-connect/certs`（JWKS: 公開鍵の配布）
- JWT のペイロード（`iss`, `sub`, `aud`, `exp`, `iat`, `azp`, スコープ）

### フェーズ 2: Resource Server を Quarkus で実装する

```
クライアント → Keycloak（トークン発行） → Quarkus API（トークン検証）
```

使用する拡張:
- `quarkus-oidc`（JWT 検証・Discovery エンドポイント自動取得）
- `@RolesAllowed`, `@Authenticated`（認可アノテーション）
- `JsonWebToken` インジェクション（ペイロードの参照）

### フェーズ 3: Authorization Server の内部を理解する

Keycloak のソースコードや仕様書（RFC）を読む。

- トークンはどう生成されるか（署名アルゴリズム: RS256 / ES256）
- リフレッシュトークンはどう管理されるか（DB or Redis）
- トークンの失効（Revocation: RFC 7009）はどう実装されるか
- イントロスペクション（RFC 7662）の仕組み

### フェーズ 4: 簡易 Authorization Server を実装する

Spring Authorization Server（Spring 公式）または Quarkus で自作。

実装する機能:
- Authorization Code Flow（PKCE）
- JWT 発行（RS256）
- JWKS エンドポイント
- トークン失効

---

## 技術スタック案

| 項目 | 選択肢 |
|------|-------|
| Authorization Server（学習用） | Keycloak（Docker） |
| Authorization Server（実装） | Spring Authorization Server |
| Resource Server | Quarkus + quarkus-oidc |
| DB | PostgreSQL |
| インフラ | Docker Compose（ローカル）|
| CI | GitHub Actions |

---

## 参照すべき仕様・資料

- [RFC 6749: The OAuth 2.0 Authorization Framework](https://www.rfc-editor.org/rfc/rfc6749)
- [RFC 7519: JSON Web Token (JWT)](https://www.rfc-editor.org/rfc/rfc7519)
- [OpenID Connect Core 1.0](https://openid.net/specs/openid-connect-core-1_0.html)
- [RFC 7009: OAuth 2.0 Token Revocation](https://www.rfc-editor.org/rfc/rfc7009)
- [RFC 7662: OAuth 2.0 Token Introspection](https://www.rfc-editor.org/rfc/rfc7662)
- [Quarkus OIDC ガイド](https://quarkus.io/guides/security-oidc-bearer-token-authentication)
- AWS Summit Tokyo 2023: ニンテンドーアカウント リノベーションプロジェクト

---

## 最初にやること

1. リポジトリ作成（`auth-learning` など）
2. Docker Compose で Keycloak を起動
3. Realm / Client / User を作成
4. curl で Authorization Code Flow を完走させる
5. JWT をデコードして各クレームを確認する

ここまでやれば「トークンとは何か」が体験として理解できる。
