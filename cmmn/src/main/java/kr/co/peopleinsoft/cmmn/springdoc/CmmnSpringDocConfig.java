package kr.co.peopleinsoft.cmmn.springdoc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CmmnSpringDocConfig {
	@Bean
	OpenAPI openAPI() {
		// OpenAPI Information
		Info openApiInfo = new Info();
		openApiInfo.title("Sample API")
			.version("1.0.0")
			.description("Sample API Description")
			.contact(new Contact()
				.name("to email@isweb.kr (Byeong-gwon, Kang)")
				.email("email@isweb.kr"));

		// Security Schema Name
		String securitySchemeName = "jwtAuth";
		// SecurityRequirement 설정
		SecurityRequirement securityRequirement = new SecurityRequirement();
		// API 요청헤더에 인증정보 포함
		securityRequirement.addList(securitySchemeName);

		SecurityScheme securityScheme = new SecurityScheme()
			.name("Authorization")
			.type(SecurityScheme.Type.HTTP) // HTTP, APIKEY, OAUTH2, OpenIdConnect ...
			.in(SecurityScheme.In.HEADER)
			.scheme("bearer ")
			.bearerFormat("JWT");

		return new OpenAPI()
			.info(openApiInfo)
			.addSecurityItem(securityRequirement)
			.schemaRequirement(securitySchemeName, securityScheme);
	}

	@Bean
	GroupedOpenApi bidPublicInfoServiceGroup() {
		return GroupedOpenApi.builder()
			.group("나라장터 입찰공고정보서비스(BidPublicInfoService)")
			.pathsToMatch ("/g2b/BidPublicInfoService/**")
			.build();
	}

	@Bean
	GroupedOpenApi orderPlanSttusServiceGroup() {
		return GroupedOpenApi.builder()
			.group("나라장터 발주계획현황서비스(OrderPlanSttusService)")
			.pathsToMatch ("/g2b/orderPlanSttusService/**")
			.build();
	}

	@Bean
	GroupedOpenApi hrcspSsstndrdInfoServiceGroup() {
		return GroupedOpenApi.builder()
			.group("나라장터 사전규격정보서비스(HrcspSsstndrdInfoService)")
			.pathsToMatch ("/g2b/hrcspSsstndrdInfoService/**")
			.build();
	}

	@Bean
	GroupedOpenApi scsbidInfoServiceGroup() {
		return GroupedOpenApi.builder()
			.group("나라장터 낙찰정보서비스(ScsbidInfoService)")
			.pathsToMatch ("/g2b/scsbidInfoService/**")
			.build();
	}

	@Bean
	GroupedOpenApi usrInfoServiceGroup() {
		return GroupedOpenApi.builder()
			.group("나라장터 사용자정보서비스(UsrInfoService)")
			.pathsToMatch ("/g2b/usrInfoService/**")
			.build();
	}

	@Bean
	GroupedOpenApi schedulerInfoServiceGroup() {
		return GroupedOpenApi.builder()
			.group("스케줄러 서비스")
			.pathsToMatch ("/shcduler/**")
			.build();
	}

	/**
	 * API 호출 시 기본으로 포함되어야 하는 파라메터의 전역 설정 (선택)
	 * JWT 의 인증정보등을 Header 에 포함하여 호출하는 등 작업 시 설정하여 사용 가능
	 * 그룹설정시.addOperationCustomizer 로 추가 가능

	@Bean
	OperationCustomizer operationCustomizer() {
		return (operation, handlerMethod) -> {
			 Parameter parameter = new Parameter()
			 .in(ParameterIn.HEADER.toString())
			 .schema(new StringSchema()._default("SAMPLE_API01").name("API_ID"))
			 .name("AppID")
			 .description("Sample API Description")
			 .required(true);
			 operation.addParametersItem(parameter);
			 return operation;
			return operation;
		};
	}
		*/
}