# quarkus-todo

Quarkus の学習を目的とした Todo アプリ。

## 構成

| サービス | 技術 | ポート |
|----------|------|--------|
| バックエンド | Quarkus（RESTEasy Reactive + Hibernate ORM Panache） | 8080 |
| フロントエンド | Next.js | 3000 |
| DB | PostgreSQL 17 | 5432 |

## 前提

[Colima](https://github.com/abiosoft/colima) が起動していること。

```bash
colima start
```

## 開発

ホットリロードを使いながらバックエンドを開発する。

```bash
cd backend
./mvnw quarkus:dev
```

ファイルを保存すると次のリクエスト時に自動で反映される。

フロントエンドは別途起動する。

```bash
cd frontend
npm run dev
```

## 本番相当の動作確認

フルスタックをコンテナで起動して確認する。ネイティブビルドが走るため数分かかる。

```bash
docker compose up --build
```

アクセス先: http://localhost:3000

## テスト

```bash
cd backend
./mvnw test
```
