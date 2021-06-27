package nextstep.subway.line.ui;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.http.MediaType;

import static java.lang.String.format;

public class LineControllerTestSnippet {

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse 지하철_노선_검색_요청(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .when()
                .formParam("name", lineRequest.getName())
                .formParam("color", lineRequest.getColor())
                .get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse 지하철_노선_PK_조건_조회_요청(Long id) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse 지하철_노선_수정_요청(Long id, LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .patch("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse 지하철_노선_삭제_요청(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse 지하철_노선_구간_추가_요청(SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(format("/lines/%d/sections", sectionRequest.getLineId()))
                .then().log().all()
                .extract();
    }
}