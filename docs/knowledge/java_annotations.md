# Java のアノテーション

## 概要

アノテーションは `@` で始まるメタデータ。コード自体の処理ロジックではなく、「このコードをどう扱うか」という指示をフレームワークに伝えるもの。アノテーション自体は何もせず、フレームワークが読み取って処理を代わりに行う。

## 具体例（`@Transactional`）

アノテーションがない場合は自分でこう書く必要がある：

```java
EntityTransaction tx = em.getTransaction();
try {
    tx.begin();
    todo.persist();
    tx.commit();
} catch (Exception e) {
    tx.rollback();
    throw e;
}
```

`@Transactional` を付けるだけでこれが不要になる。

## このプロジェクトで使われているアノテーション

| アノテーション | フレームワークが行う処理 |
|---|---|
| `@Entity` | このクラスを DB テーブルと対応付ける |
| `@GET` | HTTP GET リクエストをこのメソッドに振り分ける |
| `@Transactional` | このメソッドをトランザクション内で実行し、終わったらコミットする |
| `@Column(nullable = false)` | DB カラムに NOT NULL 制約を付ける |

## Java 全般への適用

アノテーション自体は Java 5 から導入された Java 全般の機能。ただしアノテーションを読み取って処理するのはフレームワーク側なので、フレームワークなしでは何も起きない。

| フレームワーク | アノテーションの例 |
|---|---|
| Quarkus / Jakarta EE | `@GET`、`@Transactional`、`@Entity` |
| Spring | `@GetMapping`、`@Transactional`、`@Entity` |
| JUnit（テスト） | `@Test`、`@Order` |

Spring も同じ思想でアノテーションを多用しており、Java のエコシステム全体でこのパターンが広く使われている。
