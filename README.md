# initialize-project
Initialize project &amp; git commit style

# Development Environment

## Local Development tools
- [Docker](https://docs.docker.com/engine/install/)  
- [Docker Compose](https://docs.docker.com/compose/install/)  
- JDK 11  
  1. [AdoptOpenJDK](https://adoptopenjdk.net/)  
  2. [Community builds using source code from OpenJDK project](https://github.com/ojdkbuild/ojdkbuild)  
  3. [Amazon Corretto](https://aws.amazon.com/tw/corretto/)  
  4. [sdkman](https://sdkman.io/install)  
     [方便管理JDK版本工具sdkman](https://blog.samzhu.dev/2020/12/13/%E6%96%B9%E4%BE%BF%E7%AE%A1%E7%90%86JDK%E7%89%88%E6%9C%AC%E5%B7%A5%E5%85%B7sdkman/)  

## Local IDE - VSCode
[VSCode](https://code.visualstudio.com/)  
- [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)  
- [Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-boot-dev-pack)  
- [Lombok Annotations Support for VS Code](https://marketplace.visualstudio.com/items?itemName=GabrielBB.vscode-lombok)  

## Cloud IDE - Gitpod
[Gitpod](https://www.gitpod.io/)  
- [Gitpod - Dev Environments in a Browser Tab](https://chrome.google.com/webstore/detail/gitpod-dev-environments-i/dodmmooeoklaejobgleioelladacbeki)  

# 下載專案模板
[start.spring.io](https://start.spring.io/#!type=gradle-project&language=java&platformVersion=2.4.4.RELEASE&packaging=jar&jvmVersion=11&groupId=com.example&artifactId=demo&name=demo&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.demo&dependencies=web,native,data-rest,data-jpa,mysql,flyway,validation,actuator,lombok,prometheus,cloud-starter-sleuth,testcontainers,restdocs,cloud-contract-verifier,cloud-contract-stub-runner,cloud-feign)  

# 調整 .gitignore
可參考此專案的 .gitignore  

Spting 設定檔部分只上傳開發環境使用 & 有用到通訊加密兩種配置的設定檔 如下
``` .gitignore
!**/application-dev.yml
!**/application-dev-secret.yml
```

# Coding style
使用 [spotless](https://github.com/diffplug/spotless/tree/main/plugin-gradle), 
build.gradle 增加 plugins 跟配置
``` groovy
plugins {
	id 'com.diffplug.spotless' version '5.11.1'
}

spotless {
	encoding 'UTF-8' // all formats will be interpreted as UTF-8
	java {
		target 'src/*/java/**/*.java'
		removeUnusedImports()
		importOrder()
		googleJavaFormat()
	}
	sql {
		target 'src/main/resources/**/*.sql'
	}
	groovyGradle {
		target '*.gradle'
		greclipse()
	}
}
```

gradle 指令
``` bash
# 編譯時期會先檢查
./gradlew build
# 依照指定方式格式化
./gradlew spotlessApply
```

# Git Commit Message Format
[Angular Commit Message Format](https://github.com/angular/angular/blob/master/CONTRIBUTING.md#commit)  
[GitGuide](https://zjdoc-gitguide.readthedocs.io/zh_CN/latest/)  
[Understanding Semantic Commit Messages Using Git and Angular](https://nitayneeman.com/posts/understanding-semantic-commit-messages-using-git-and-angular/)  
  
## Commit Message Format
```
<header>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

## Commit Message Header
[Commit Message Header](https://github.com/angular/angular/blob/master/CONTRIBUTING.md#commit-message-header)
```
<type>(<scope>): <short summary>
  │       │             │
  │       │             └─⫸ Summary in present tense. Not capitalized. No period at the end.
  │       │
  │       └─⫸ Commit Scope: animations|bazel|benchpress|common|compiler|compiler-cli|core|
  │                          elements|forms|http|language-service|localize|platform-browser|
  │                          platform-browser-dynamic|platform-server|router|service-worker|
  │                          upgrade|zone.js|packaging|changelog|dev-infra|docs-infra|migrations|
  │                          ngcc|ve
  │
  └─⫸ Commit Type: build|ci|docs|feat|fix|perf|refactor|test
```

### Type
Must be one of the following:  
  
- build: Changes that affect the build system or external dependencies (example scopes: gulp, broccoli, npm)
- ci: Changes to our CI configuration files and scripts (example scopes: Circle, BrowserStack, SauceLabs)
- docs: Documentation only changes
- feat: A new feature
- fix: A bug fix
- perf: A code change that improves performance
- refactor: A code change that neither fixes a bug nor adds a feature
- test: Adding missing tests or correcting existing tests

### Scope
The scope should be the name of the npm package affected (as perceived by the person reading the changelog generated from commit messages).  
  
The following is the list of supported scopes:  
  
- animations
- bazel
- benchpress
- common
- compiler
- compiler-cli
- core
- elements
- forms
- http
- language-service
- localize
- platform-browser
- platform-browser-dynamic
- platform-server
- router
- service-worker
- upgrade
- zone.js

### Summary
Use the summary field to provide a succinct description of the change:  

use the imperative, present tense: "change" not "changed" nor "changes"  
don't capitalize the first letter  
no dot (.) at the end  

## Commit Message Body
[Commit Message Body](https://github.com/angular/angular/blob/master/CONTRIBUTING.md#commit-message-body)  
Just as in the summary, use the imperative, present tense: "fix" not "fixed" nor "fixes".  

Explain the motivation for the change in the commit message body. This commit message should explain why you are making the change. You can include a comparison of the previous behavior with the new behavior in order to illustrate the impact of the change.  
  
Example:  
```
More detailed explanatory text, if necessary.  Wrap it to 
about 72 characters or so. 

Further paragraphs come after blank lines.

- Bullet points are okay, too
- Use a hanging indent
```

## Commit Message Footer
[Commit Message Footer](https://github.com/angular/angular/blob/master/CONTRIBUTING.md#commit-message-footer)  
The footer can contain information about breaking changes and is also the place to reference GitHub issues, Jira tickets, and other PRs that this commit closes or is related to.  
  
Example:  
```
BREAKING CHANGE: isolate scope bindings definition has changed.

    To migrate the code follow the example below:
    Before:

    scope: {
      myAttr: 'attribute',
    }

    After:

    scope: {
      myAttr: '@',
    }

    The removed `inject` wasn't generaly useful for directives so there should be no code using it.
```
or
```
Closes #123, #245, #992
```

## Revert commits
[Revert commits](https://github.com/angular/angular/blob/master/CONTRIBUTING.md#revert-commits)
Example:
```
revert: feat(pencil): add 'graphiteWidth' option

This reverts commit 667ecc1654a317a13331b17617d973392f415f02.
```

更多範例  
[阮一峰的网络日志 -Commit message 和 Change log 编写指南](https://www.ruanyifeng.com/blog/2016/01/commit_message_change_log.html)  
[Git Commit Message 這樣寫會更好，替專案引入規範與範例](https://wadehuanglearning.blogspot.com/2019/05/commit-commit-commit-why-what-commit.html)  

# Add git commit template
使用指令
``` bash
cat << 'EOF' > $HOME/.gitmessage.txt
<type>(<scope>): <subject>

<body>

<footer>
EOF

git config --global commit.template $HOME/.gitmessage.txt
```
其他工具輔助  
[IntelliJ - Git Commit Template](https://plugins.jetbrains.com/plugin/9861-git-commit-template)  

# Add git hook 自動化檢查或格式化
## 使用指令
``` bash
cat << 'EOF' > .git/hooks/pre-commit
#!/bin/bash

PWD=`pwd`

globalEmail=`git config --global --get user.email`

echo "Global commit email: "$globalEmail""
exit 0
EOF

chmod +x .git/hooks/pre-commit
```

## commitlint
[commitlint](https://github.com/conventional-changelog/commitlint#readme), [config-angular](https://www.npmjs.com/package/@commitlint/config-angular)  
  
Install test  
``` bash
npm install -g @commitlint/config-angular @commitlint/cli
echo "module.exports = {extends: ['@commitlint/config-angular']};" > commitlint.config.js

echo "foo: some message" | npx commitlint # fails
echo "fix: some message" | npx commitlint # passes
```

Uninstall
``` bash
npm uninstall -g @commitlint/config-angular @commitlint/cli
```

# Project directory
``` bash
# 需要上傳至 git
touch src/main/resources/.gitkeep

# Springboot 使用設定檔(不會包到 jar 檔中)
mkdir config
touch config/application-dev.yml
touch config/application-dev-secret.yml

# 說明文件
mkdir docs

# 外部開發資源
mkdir docker
cat << 'EOF' > docker/docker-compose.yml
version: '3.9'
services:
  mysql:
    image: 'mysql:8.0.23'
    restart: always
    command: '--default-authentication-plugin=mysql_native_password'
    ports:
      - '3306:3306'
    environment:
      MYSQL_ROOT_PASSWORD: 'pw123456'
      MYSQL_DATABASE: 'testdb'
      MYSQL_USER: 'user1'
      MYSQL_PASSWORD: 'pw123456'
    logging:
      driver: json-file
      options:
        max-size: 20m
        max-file: '2'
EOF
```

# DB 版控
[Microservice Architecture Pattern: Database per service](https://microservices.io/patterns/data/database-per-service.html)

## 建立 projecttion 對應的 Table
[src/main/resources/db/migration/V20210417.0__basic_schema.sql](https://github.com/samzhu/2021-04-07-ddd-implementation-lab/blob/main/src/main/resources/db/migration/V1.0.0__basic_schema.sql)

## 需要的管理表
[src/main/resources/db/migration/V20210418.1__add_order_schema.sql](https://github.com/samzhu/2021-04-07-ddd-implementation-lab/blob/main/src/main/resources/db/migration/V1.1.0__axon_schema.sql)

# 建立需要的設定檔
src/main/resources/application.yml
``` yml
spring:
  application:
    name: initialize-project
  profiles:
    active:
    - dev
```

# 使用 jooq 產生 JPA
下載 [jooq](https://www.jooq.org/download/)  

## 起動需要的 Mysql
``` bash
docker-compose -f docker/docker-compose.yml up -d
```

## 啟動APP
透過 flyway 建立 DB Table 結束後停止 application

## 設定 Jooq
需要額外的依賴  
[JAXB API » 2.3.1](https://mvnrepository.com/artifact/javax.xml.bind/jaxb-api/2.3.1)  

## 執行 jooq 產生器
``` bash
java -cp jooq/jOOQ-lib/*:jooq/3rd-lib/*:jooq/db-lib/* org.jooq.codegen.GenerationTool jooq/jooq-config.xml
```
產出的 entity 在此  
src/main/java/com/example/demo/enterprise/domain/tables/pojos

## 移除用不到的 Jooq 程式碼
``` bash
rm src/main/java/com/example/demo/enterprise/domain/tables/*.java
rm src/main/java/com/example/demo/enterprise/domain/*.java
```



