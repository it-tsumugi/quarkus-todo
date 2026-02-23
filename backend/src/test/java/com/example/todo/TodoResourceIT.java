package com.example.todo;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

// @QuarkusIntegrationTest はビルド済みのネイティブバイナリを実際に起動してテストを実行する。
// @QuarkusTest（JVM モード）とは異なり、本番と同じバイナリに対してテストが走るため
// リフレクションやシリアライズの問題をビルド後に検出できる。
//
// container-build=true で生成したバイナリは Linux 向けのため、macOS 上では実行できない。
// そのため macOS ではこのテストをスキップし、CI（Linux 環境）でのみ実行する。
@QuarkusIntegrationTest
@DisabledOnOs(OS.MAC)
class TodoResourceIT extends TodoResourceTest {
}
