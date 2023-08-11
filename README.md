# 環境

## Java

```
% java --version
java 11.0.5 2019-10-15 LTS
Java(TM) SE Runtime Environment 18.9 (build 11.0.5+10-LTS)
Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.5+10-LTS, mixed mode)
```

## Gradle

```
% gradle --version

------------------------------------------------------------
Gradle 8.2.1
------------------------------------------------------------

Build time:   2023-07-10 12:12:35 UTC
Revision:     a38ec64d3c4612da9083cc506a1ccb212afeecaa

Kotlin:       1.8.20
Groovy:       3.0.17
Ant:          Apache Ant(TM) version 1.10.13 compiled on January 4 2023
JVM:          11.0.5 (Oracle Corporation 11.0.5+10-LTS)
OS:           Mac OS X 10.16 x86_64
```

# ビルド

```
% ./gradlew build
```

# 実行

```
% java -jar ./app/build/libs/app.jar
```

# 結果

`./data/` 内に結果が出力される

## `./data/index/{name}`

作成されたインデックスが出力されう

## `./data/term/{name}.txt`

作成されたタームの一覧が出力される

## `./data/searchResult/{name}/{word}.txt`

各検索単語ごとの検索結果が表示される。
対象となった単語がハイライトされる。
