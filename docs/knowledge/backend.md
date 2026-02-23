# バックエンドの技術構成

## RESTEasy Reactive

Quarkus の REST フレームワーク。Jakarta EE の JAX-RS 仕様に基づいており、アノテーションでエンドポイントを定義する。

```java
@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoResource {

    @GET
    public List<Todo> list() { ... }

    @POST
    @Transactional
    public Response create(Todo todo) { ... }
}
```

「Reactive」とついているのは、内部的にノンブロッキング I/O を使って高いスループットを実現する実装になっているため。通常の JAX-RS と書き方はほぼ同じだが、ネイティブビルドとの親和性が高い。

## Hibernate ORM Panache

ORM（Object-Relational Mapping）ライブラリ。Java のクラスと DB のテーブルを対応付け、SQL を直接書かずに DB 操作を行える。

Panache は Hibernate ORM をより簡潔に書けるようにした Quarkus 独自のラッパー。`PanacheEntity` を継承するだけで基本的な CRUD メソッドが使えるようになる。

```java
@Entity
@Table(name = "todos")
public class Todo extends PanacheEntity {
    // PanacheEntity が id（Long）を自動生成する

    @Column(nullable = false)
    public String title;

    public boolean completed = false;
}
```

```java
// PanacheEntity から継承したメソッド
Todo.findById(id)   // SELECT * FROM todos WHERE id = ?
todo.persist()      // INSERT INTO todos ...
Todo.deleteById(id) // DELETE FROM todos WHERE id = ?
```

## CDI（quarkus-arc）

CDI（Contexts and Dependency Injection）は Jakarta EE の依存性注入仕様。Quarkus では `quarkus-arc` がその実装を担う。

`@Transactional` などのインターセプターアノテーションは CDI の仕組みで動いている。メソッド呼び出しをインターセプトして、トランザクションの開始・コミット・ロールバックを自動で行う。

## DB スキーマ管理

`application.properties` でプロファイルごとにスキーマ管理の戦略を切り替えている。

```properties
# 開発時: 起動のたびにテーブルを作り直す（エンティティ変更が即反映される）
%dev.quarkus.hibernate-orm.schema-management.strategy=drop-and-create

# 本番時: 既存テーブルに差分だけ適用する（データを消さない）
%prod.quarkus.hibernate-orm.schema-management.strategy=update
```

`%dev` / `%prod` はプロファイルプレフィックスで、起動モードに応じて自動的に切り替わる。

## リクエストの処理フロー

```
HTTP リクエスト
    ↓
TodoResource（@Path でルーティング）
    ↓
Todo（PanacheEntity で DB 操作）
    ↓
PostgreSQL
    ↓
JSON レスポンス（Jackson が自動変換）
```
