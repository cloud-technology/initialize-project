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

## Commitlint
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