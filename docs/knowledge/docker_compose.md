# Docker Compose の使い分け

## なぜ環境ごとにファイルを分けるのか

1つの `docker-compose.yml` でオプション指定だけで切り替えようとすると設定が複雑になり、意図しないビルドが走るリスクがある。ファイルを分けることで「このファイルを使えばこの環境」と意図が明確になる。

## このプロジェクトの構成

| ファイル | 用途 | バックエンドのビルド |
|----------|------|------|
| `docker-compose.dev.yml` | 開発・動作確認 | JVM モード（速い） |
| `docker-compose.yml` | 本番相当の確認 | ネイティブビルド（数分） |

## JVM モード用の Dockerfile を別途作った理由

`Dockerfile.jvm`（Quarkus が生成するデフォルト）はビルド済みの JAR（`target/quarkus-app/`）を前提としており、`docker compose up --build` だけでは完結しない。

そのため `Dockerfile.jvm-dev` をマルチステージビルドで作成した。

```
ステージ1（builder）: eclipse-temurin:17 で Maven ビルドを実行
    ↓
ステージ2（runner）: ビルド成果物だけをコピーして起動
```

ステージを分けることで最終イメージに Maven や JDK が含まれず、サイズを小さくできる。

## 依存関係のキャッシュ

`Dockerfile.jvm-dev` では以下の順でコピーしている：

```dockerfile
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./
RUN ./mvnw dependency:resolve   # ← ここでキャッシュが効く

COPY src ./src
RUN ./mvnw package -DskipTests
```

Docker はレイヤーをキャッシュする。`src/` を変更しても `pom.xml` が変わっていなければ依存関係のダウンロードはスキップされ、2回目以降のビルドが速くなる。

## DOCKER_HOST の設定

Colima は Docker デーモンのソケットをデフォルト（`/var/run/docker.sock`）ではなく `~/.colima/default/docker.sock` に作る。`DOCKER_HOST` 環境変数でソケットの場所を Docker CLI に伝える必要がある。

プロジェクトルートの `.env` に記述することで、毎回コマンドに書かなくて済む。Docker Compose は同じディレクトリの `.env` を自動で読む。

```
# .env
DOCKER_HOST=unix:///Users/tsumugi/.colima/default/docker.sock
```

`.env` は `.gitignore` に含めてあるため Git には入らない（マシン固有の設定のため）。

## 起動コマンドまとめ

```bash
# 開発・動作確認
docker compose -f docker-compose.dev.yml up --build

# 本番相当の確認
docker compose up --build

# 2回目以降（イメージが既にある場合）は --build を省略可
docker compose -f docker-compose.dev.yml up
```
