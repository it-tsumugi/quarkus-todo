# quarkus-todo

## 目的
Quarkus 本番活用の知識習得。Todo アプリは手段。重点領域: **認証・認可（OIDC/OAuth2/JWT）**（認証システム部署への異動を見据えている）

## 方針
- 「なぜそう書くのか」の理解を優先する
- Quarkus 慣用的な書き方（CDI、MicroProfile）に従う
- 本番を意識した構成を選択する

## 技術スタック
| 項目 | 選択 |
|------|------|
| バックエンド | Quarkus / RESTEasy Reactive / Java / Maven |
| DB | PostgreSQL |
| フロントエンド | Next.js（バックエンドと完全分離） |
| 認証 | CRUD 完成後に Keycloak + OIDC を追加 |
| 本番ビルド | GraalVM ネイティブのみ |
| CI/CD | GitHub Actions |
| リポジトリ | モノリポ（backend / frontend） |

## 知識の記録
技術的な概念・仕組みを説明したら、指示がなくても Task ツールで `general-purpose` エージェントを使い `docs/knowledge/` に保存する。「なぜそうなるのか」を中心に記述する。

## 注意事項
- 学習の文脈を大切にする
- ショートカットや魔法的な設定は避け、仕組みを理解してから使う
