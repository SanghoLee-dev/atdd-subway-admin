package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.section.SectionAcceptanceTest.ExpectSectionResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.dto.SectionDeleteResponse;
import nextstep.subway.station.dto.StationRequest;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static java.lang.String.format;
import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.section.SectionAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("구간 테스트")
class SectionDeleteAcceptanceTest extends AcceptanceTest {

    private final Long 강남역_역삼역_ID = 1L;
    private final Long 역삼역_수진역_ID = 2L;
    private final Long 수진역_모란역_ID = 3L;
    private final Long 모란역_야탑역_ID = 4L;

    @TestFactory
    @DisplayName("중간역을 삭제한다" +
            "강남역 - 역삼역 - 수진역 - 모란역 =>" +
            "강남역 - 수진역 - 모란역 =>" +
            "강남역 - 모란역")
    Stream<DynamicTest> 중간역을_삭제한다() {
        Long 신규_강남역_수진역_ID = 1L;
        Long 신규_강남역_모란역_ID = 1L;
        return Stream.of(
                dynamicTest("강남역을 추가한다", 지하철역_생성_요청_및_체크(강남역, 강남역_ID)),
                dynamicTest("역삼역을 추가한다", 지하철역_생성_요청_및_체크(역삼역, 역삼역_ID)),
                dynamicTest("수진역을 추가한다", 지하철역_생성_요청_및_체크(수진역, 수진역_ID)),
                dynamicTest("모란역을 추가한다", 지하철역_생성_요청_및_체크(모란역, 모란역_ID)),
                dynamicTest("(상)강남역과 (하)역삼역의 노선을 만든다",
                        라인_생성_및_체크(분당_라인, 분당_라인_ID, new StationRequest[]{강남역, 역삼역})
                ),
                dynamicTest("(상)역삼역과 (하)수진역을 연결한다", 구간_생성_및_체크(역삼역_수진역_길이_1, 분당_라인_ID, 역삼역_수진역_ID)),
                dynamicTest("(상)수진역과 (하)모란역을 연결한다", 구간_생성_및_체크(수진역_모란역_길이_1, 분당_라인_ID, 수진역_모란역_ID)),
                dynamicTest("분당라인의 전체 연결을 확인한다", 분당라인_기본_연결_및_길이_체크()),
                dynamicTest("역삼역을 삭제한다", 구간_역_삭제_및_체크(분당_라인_ID, 역삼역_ID, 역삼역_수진역_ID)),
                dynamicTest("역삼역을 삭제 후 전체 라인을 검사한다", 전체_연결_확인(
                        분당_라인_ID,
                        new ExpectSectionResponse(신규_강남역_수진역_ID, 강남역_ID, 수진역_ID,
                                분당_라인.getDistance() + 역삼역_수진역_길이_1.getDistance()),
                        new ExpectSectionResponse(수진역_모란역_ID, 수진역_ID, 모란역_ID, 수진역_모란역_길이_1.getDistance())
                )),
                dynamicTest("수진역을 삭제한다", 구간_역_삭제_및_체크(분당_라인_ID, 수진역_ID, 수진역_모란역_ID)),
                dynamicTest("수진역을 삭제 후 전체 라인을 검사한다", 전체_연결_확인(
                        분당_라인_ID,
                        new ExpectSectionResponse(신규_강남역_모란역_ID, 강남역_ID, 모란역_ID,
                                분당_라인.getDistance() + 역삼역_수진역_길이_1.getDistance() + 수진역_모란역_길이_1.getDistance())
                        )
                )
        );
    }

    @TestFactory
    @DisplayName("상행역을 삭제한다" +
            "강남역 - 역삼역 - 수진역 - 모란역 =>" +
            "역삼역 - 수진역 - 모란역 =>" +
            "수진역 - 모란역")
    Stream<DynamicTest> 상행역을_삭제한다() {
        return Stream.of(
                dynamicTest("강남역을 추가한다", 지하철역_생성_요청_및_체크(강남역, 강남역_ID)),
                dynamicTest("역삼역을 추가한다", 지하철역_생성_요청_및_체크(역삼역, 역삼역_ID)),
                dynamicTest("수진역을 추가한다", 지하철역_생성_요청_및_체크(수진역, 수진역_ID)),
                dynamicTest("모란역을 추가한다", 지하철역_생성_요청_및_체크(모란역, 모란역_ID)),
                dynamicTest("(상)강남역과 (하)역삼역의 노선을 만든다",
                        라인_생성_및_체크(분당_라인, 분당_라인_ID, new StationRequest[]{강남역, 역삼역})
                ),
                dynamicTest("(상)역삼역과 (하)수진역을 연결한다", 구간_생성_및_체크(역삼역_수진역_길이_1, 분당_라인_ID, 역삼역_수진역_ID)),
                dynamicTest("(상)수진역과 (하)모란역을 연결한다", 구간_생성_및_체크(수진역_모란역_길이_1, 분당_라인_ID, 수진역_모란역_ID)),
                dynamicTest("분당라인의 전체 연결을 확인한다", 분당라인_기본_연결_및_길이_체크()),
                dynamicTest("강남역을 삭제한다", 구간_역_삭제_및_체크(분당_라인_ID, 강남역_ID, 강남역_역삼역_ID)),
                dynamicTest("강남역을 삭제 후 전체 라인을 검사한다", 전체_연결_확인(
                        분당_라인_ID,
                        new ExpectSectionResponse(역삼역_수진역_ID, 역삼역_ID, 수진역_ID, 역삼역_수진역_길이_1.getDistance()),
                        new ExpectSectionResponse(수진역_모란역_ID, 수진역_ID, 모란역_ID, 수진역_모란역_길이_1.getDistance())
                )),
                dynamicTest("역삼역을 삭제한다", 구간_역_삭제_및_체크(분당_라인_ID, 역삼역_ID, 역삼역_수진역_ID)),
                dynamicTest("역삼역을 삭제 후 전체 라인을 검사한다", 전체_연결_확인(
                        분당_라인_ID,
                        new ExpectSectionResponse(수진역_모란역_ID, 수진역_ID, 모란역_ID, 수진역_모란역_길이_1.getDistance()))
                )
        );
    }

    @TestFactory
    @DisplayName("하행역을 삭제한다 강남역 - 역삼역 - 수진역 - 모란역 => " +
            "강남역 - 역삼역 - 수진역 =>" +
            "강남역 - 역삼역")
    Stream<DynamicTest> 하행역을_삭제한다() {
        return Stream.of(
                dynamicTest("강남역을 추가한다", 지하철역_생성_요청_및_체크(강남역, 강남역_ID)),
                dynamicTest("역삼역을 추가한다", 지하철역_생성_요청_및_체크(역삼역, 역삼역_ID)),
                dynamicTest("수진역을 추가한다", 지하철역_생성_요청_및_체크(수진역, 수진역_ID)),
                dynamicTest("모란역을 추가한다", 지하철역_생성_요청_및_체크(모란역, 모란역_ID)),
                dynamicTest("(상)강남역과 (하)역삼역의 노선을 만든다",
                        라인_생성_및_체크(분당_라인, 분당_라인_ID, new StationRequest[]{강남역, 역삼역})
                ),
                dynamicTest("(상)역삼역과 (하)수진역을 연결한다", 구간_생성_및_체크(역삼역_수진역_길이_1, 분당_라인_ID, 역삼역_수진역_ID)),
                dynamicTest("(상)수진역과 (하)모란역을 연결한다", 구간_생성_및_체크(수진역_모란역_길이_1, 분당_라인_ID, 수진역_모란역_ID)),
                dynamicTest("분당라인의 전체 연결을 확인한다", 분당라인_기본_연결_및_길이_체크()),
                dynamicTest("모란역을 삭제한다", 구간_역_삭제_및_체크(분당_라인_ID, 모란역_ID, 수진역_모란역_ID)),
                dynamicTest("모란역을 삭제 후 전체 라인을 검사한다", 전체_연결_확인(
                        분당_라인_ID,
                        new ExpectSectionResponse(강남역_역삼역_ID, 강남역_ID, 역삼역_ID, 분당_라인.getDistance()),
                        new ExpectSectionResponse(역삼역_수진역_ID, 역삼역_ID, 수진역_ID, 역삼역_수진역_길이_1.getDistance())
                )),
                dynamicTest("수진역을 삭제한다", 구간_역_삭제_및_체크(분당_라인_ID, 수진역_ID, 역삼역_수진역_ID)),
                dynamicTest("수진역을 삭제 후 전체 라인을 검사한다", 전체_연결_확인(
                        분당_라인_ID,
                        new ExpectSectionResponse(강남역_역삼역_ID, 강남역_ID, 역삼역_ID, 분당_라인.getDistance())
                )
        ));
    }

    @TestFactory
    @DisplayName("복잡하게 삭제한다 강남역 - 역삼역 - 수진역 - 모란역 - 야탑역 => " +
            "역삼역 - 수진역 - 모란역 - 야탑역 =>" +
            "역삼역 - 모란역 - 야탑역 =>" +
            "역삽역 - 모란역")
    Stream<DynamicTest> 복잡하게_삭제한다() {
        Long 신규_역삼역_모란역_ID = 역삼역_수진역_ID;

        return Stream.of(
                dynamicTest("강남역을 추가한다", 지하철역_생성_요청_및_체크(강남역, 강남역_ID)),
                dynamicTest("역삼역을 추가한다", 지하철역_생성_요청_및_체크(역삼역, 역삼역_ID)),
                dynamicTest("수진역을 추가한다", 지하철역_생성_요청_및_체크(수진역, 수진역_ID)),
                dynamicTest("모란역을 추가한다", 지하철역_생성_요청_및_체크(모란역, 모란역_ID)),
                dynamicTest("야탑역을 추가한다", 지하철역_생성_요청_및_체크(야탑역, 야탑역_ID)),
                dynamicTest("(상)강남역과 (하)역삼역의 노선을 만든다",
                        라인_생성_및_체크(분당_라인, 분당_라인_ID, new StationRequest[]{강남역, 역삼역})
                ),
                dynamicTest("(상)역삼역과 (하)수진역을 연결한다", 구간_생성_및_체크(역삼역_수진역_길이_1, 분당_라인_ID, 역삼역_수진역_ID)),
                dynamicTest("(상)수진역과 (하)모란역을 연결한다", 구간_생성_및_체크(수진역_모란역_길이_1, 분당_라인_ID, 수진역_모란역_ID)),
                dynamicTest("(상)모란역과 (하)야탑역을 연결한다", 구간_생성_및_체크(모란역_야탑역_길이_1, 분당_라인_ID, 모란역_야탑역_ID)),
                dynamicTest("야탑역 추가된 분당라인의 전체 연결을 확인한다", 야탑역_추가_분당라인_기본_연결_및_길이_체크()),
                dynamicTest("강남역을 삭제한다", 구간_역_삭제_및_체크(분당_라인_ID, 강남역_ID, 강남역_역삼역_ID)),
                dynamicTest("강남역을 삭제 후 전체 라인을 검사한다", 전체_연결_확인(
                        분당_라인_ID,
                        new ExpectSectionResponse(역삼역_수진역_ID, 역삼역_ID, 수진역_ID, 역삼역_수진역_길이_1.getDistance()),
                        new ExpectSectionResponse(수진역_모란역_ID, 수진역_ID, 모란역_ID, 수진역_모란역_길이_1.getDistance()),
                        new ExpectSectionResponse(모란역_야탑역_ID, 모란역_ID, 야탑역_ID, 모란역_야탑역_길이_1.getDistance())
                    )
                ),
                dynamicTest("수진역을 삭제한다", 구간_역_삭제_및_체크(분당_라인_ID, 수진역_ID, 수진역_모란역_ID)),
                dynamicTest("수진역을 삭제 후 전체 라인을 검사한다", 전체_연결_확인(
                        분당_라인_ID,
                        new ExpectSectionResponse(신규_역삼역_모란역_ID, 역삼역_ID, 모란역_ID,
                                역삼역_수진역_길이_1.getDistance() + 수진역_모란역_길이_1.getDistance()),
                        new ExpectSectionResponse(모란역_야탑역_ID, 모란역_ID, 야탑역_ID, 모란역_야탑역_길이_1.getDistance())
                    )
                ),
                dynamicTest("야탑역을 삭제한다", 구간_역_삭제_및_체크(분당_라인_ID, 야탑역_ID, 모란역_야탑역_ID)),
                dynamicTest("야탑역을 삭제 후 전체 라인을 검사한다", 전체_연결_확인(
                        분당_라인_ID,
                        new ExpectSectionResponse(신규_역삼역_모란역_ID, 역삼역_ID, 모란역_ID,
                                역삼역_수진역_길이_1.getDistance() + 수진역_모란역_길이_1.getDistance())
                    )
                )
        );
    }

    @TestFactory
    @DisplayName("노선에 등록이 안되어잇는 역을 제거하려 할 때는 문제가 발생한다")
    Stream<DynamicTest> 노선에_등록이_안되어있는_역을_제거하려_할_대는_문제가_발생한다() {
        return Stream.of(
                dynamicTest("강남역을 추가한다", 지하철역_생성_요청_및_체크(강남역, 강남역_ID)),
                dynamicTest("역삼역을 추가한다", 지하철역_생성_요청_및_체크(역삼역, 역삼역_ID)),
                dynamicTest("수진역을 추가한다", 지하철역_생성_요청_및_체크(수진역, 수진역_ID)),
                dynamicTest("(상)강남역과 (하)역삼역의 노선을 만든다",
                        라인_생성_및_체크(분당_라인, 분당_라인_ID, new StationRequest[]{강남역, 역삼역})
                ),
                dynamicTest("등록이 안된 수진역을 삭제하면 에러가 발생한다", () -> {
                    ExtractableResponse<Response> response = 구간_역_삭제_요청(분당_라인_ID, 수진역_ID);

                    구간_역_삭제_실패_검증(response);
                })
        );
    }

    @TestFactory
    @DisplayName("구간이 1개일 때 삭제하려고 할 시 문제가 발생한다")
    Stream<DynamicTest> 구간이_1개일_때_삭제하려고_할_시_문제가_발생한다() {
        return Stream.of(
                dynamicTest("강남역을 추가한다", 지하철역_생성_요청_및_체크(강남역, 강남역_ID)),
                dynamicTest("역삼역을 추가한다", 지하철역_생성_요청_및_체크(역삼역, 역삼역_ID)),
                dynamicTest("(상)강남역과 (하)역삼역의 노선을 만든다",
                        라인_생성_및_체크(분당_라인, 분당_라인_ID, new StationRequest[]{강남역, 역삼역})
                ),
                dynamicTest("구간이 1개만 남았을 때 삭제하려고 하면 문제가 발생한다", () -> {
                    ExtractableResponse<Response> response = 구간_역_삭제_요청(분당_라인_ID, 역삼역_ID);

                    구간_역_삭제_실패_검증(response);
                })
        );
    }

    public static Executable 구간_역_삭제_및_체크(Long lineId, Long stationId, Long expectDeletedSectionId) {
        return () -> {
            ExtractableResponse<Response> response = 구간_역_삭제_요청(lineId, stationId);

            구간_역_삭제_헤더_검증(response);

            SectionDeleteResponse sectionDeleteResponse = response.as(SectionDeleteResponse.class);

            구간_역_삭제_본문_검증(sectionDeleteResponse, expectDeletedSectionId);
        };
    }

    public static  ExtractableResponse<Response> 구간_역_삭제_요청(Long lineId, Long stationId) {
        ExtractableResponse<Response> response = RestAssured.given()
                .log().all()
                .when()
                .log().all()
                .delete(format("/lines/%d/sections?stationId=%d", lineId, stationId))
                .then().extract();
        return response;
    }

    private static void 구간_역_삭제_헤더_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.OK.value());
        assertThat(response.header(HttpHeaders.CONTENT_TYPE))
                .isIn(ContentType.JSON.getContentTypeStrings());
    }

    private static void 구간_역_삭제_실패_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private static void 구간_역_삭제_본문_검증(SectionDeleteResponse sectionDeleteResponse, Long expectDeletedSectionId) {
        assertThat(sectionDeleteResponse.getId())
                .isEqualTo(expectDeletedSectionId);
    }

    private Executable 분당라인_기본_연결_및_길이_체크() {
        return 전체_연결_확인(
                분당_라인_ID,
                new ExpectSectionResponse(강남역_역삼역_ID, 강남역_ID, 역삼역_ID, 분당_라인.getDistance()),
                new ExpectSectionResponse(역삼역_수진역_ID, 역삼역_ID, 수진역_ID, 역삼역_수진역_길이_1.getDistance()),
                new ExpectSectionResponse(수진역_모란역_ID, 수진역_ID, 모란역_ID, 수진역_모란역_길이_1.getDistance())
        );
    }


    private Executable 야탑역_추가_분당라인_기본_연결_및_길이_체크() {
        return 전체_연결_확인(
                분당_라인_ID,
                new ExpectSectionResponse(강남역_역삼역_ID, 강남역_ID, 역삼역_ID, 분당_라인.getDistance()),
                new ExpectSectionResponse(역삼역_수진역_ID, 역삼역_ID, 수진역_ID, 역삼역_수진역_길이_1.getDistance()),
                new ExpectSectionResponse(수진역_모란역_ID, 수진역_ID, 모란역_ID, 수진역_모란역_길이_1.getDistance()),
                new ExpectSectionResponse(모란역_야탑역_ID, 모란역_ID, 야탑역_ID, 모란역_야탑역_길이_1.getDistance())
        );
    }
}