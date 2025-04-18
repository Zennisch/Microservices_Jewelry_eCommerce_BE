plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.iuh.edu.fit"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("com.google.auth:google-auth-library-oauth2-http:1.22.0")
	implementation("com.google.api-client:google-api-client:2.2.0")
    implementation("com.google.http-client:google-http-client-gson:1.43.3")
    implementation("com.google.oauth-client:google-oauth-client:1.34.1")
	implementation("com.google.api-client:google-api-client-gson:1.31.1") 
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("com.auth0:java-jwt:4.4.0")
	implementation("com.turkraft.springfilter:jpa:3.1.7")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}


sourceSets {
    main {
        java.srcDirs("src/main/java")
    }
}