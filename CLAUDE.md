# CLAUDE.md

必ず日本語で回答すること。

## プロジェクト仕様
- このプロジェクトには2つのアプリケーションを構築する
  - 認証・認可サーバ
    - Dockerでkeycloak環境を構築
    - 公開ドメインは `https://localhost:9000`
    - OAuth2のClient Credential Grant, Authorization Code Grant, Refresh flow Grantをサポートする
    - 認証はID/PWのみ
      - 初期パスワード変更は不要
    - Keycloakの管理者ユーザー
      - ID: admin
      - PW: admin
  - リソースサーバー
    - 認可が不要なAPI
      - base path: `/api/guest`
    - 認可が必要なAPI
      - base path: `/api/pro`
      - OAuth2 認可コードフローによる認可が必要
      - `read_todos` scope が必要なAPI
      - `write_todos` scope が必要なAPI
      - `openid` scope が必要なAPI
    - 公開ドメインは `https://localhost:8080`

### アーキテクチャ
- 言語はKotlin
- フレームワークはSpring Boot
- DBにH2を利用
- クリーンアーキテクチャの原則に従ったSpring Boot Kotlinアプリケーション
- 依存関係は以下の通り
  - Controller → UseCase → Repository → Model

### Spring Bootの開発方針
- 設定はyaml形式にする
- Spring Data JPAの機能を最大限活用し、極力SQLを書かない
  - メソッド命名規則（findBy...、OrderBy...など）を使用してクエリを自動生成
  - 複雑なクエリが必要な場合のみ`@Query`アノテーションを使用
  - Native SQLは特別な理由がない限り使用しない（例外: ランダム取得など） 

## Keycloak設定

Keycloakの詳細な設定手順は [KEYCLOAK_SETUP.md](./KEYCLOAK_SETUP.md) を参照してください。

- **管理コンソール**: http://localhost:9000/admin/
- **管理者ユーザー**: admin / admin
- **Realm名**: study-authz
- **クライアント**:
  - `resource-server`: Authorization Code Flow用
  - `service-client`: Client Credentials Flow用
- **スコープ**: `read_todos`, `write_todos`, `openid`
- **テストユーザー**: testuser / password

## ビルドと開発コマンド

### 必須コマンド
- **プロジェクトのビルド**: `./gradlew build`
- **テストの実行**: `./gradlew test`
- **アプリケーションの起動**: `./gradlew bootRun`
- **ビルド成果物のクリーン**: `./gradlew clean`
- **特定のテストクラスの実行**: `./gradlew test --tests "com.example.demo.usecase.TodoUseCaseTest"`
- **依存関係の確認**: `./gradlew dependencies`
- **ビルド結果の確認**: `./gradlew build --scan`
- **Docker環境の初回起動**: `docker compose up -d`
- **Docker環境の一時停止**: `docker compose stop`
- **Docker環境の再開**: `docker compose start`
- **Docker環境の停止**: `docker compose down`

## 開発ワークフロー

### リファクタリング手順
1. **gitブランチを作成**
   ```bash
   git checkout -b refactor/feature-name
   ```

2. **リファクタリングを実施**
   - コードの改善を行う

3. **テストを動かし影響がないことを確認**
   ```bash
   ./gradlew test
   ```

4. **テストが失敗した場合**
   - 原因を調査・修正して手順3に戻る

5. **テストが成功した場合、commit**
   ```bash
   git add .
   git commit -m "refactor: 変更内容の説明"
   ```

6. **アプリケーションを起動して動作することを確認する**
   ```bash
   ./gradlew bootRun
   # 別ターミナルで動作確認
   curl -s "http://localhost:8080/api/guest/todos" | jq .
   ```

7. **アプリケーションが正しく動作しない場合**
   - 原因を調査・修正して手順3に戻る

8. **アプリケーションが正しく動作した場合**
   - アプリケーションを停止（Ctrl+C）
   - gitブランチをmainブランチにマージする
   ```bash
   git checkout main
   git merge refactor/feature-name
   git branch -d refactor/feature-name
   ```

## 動作確認

### リソースサーバーの起動と確認
```bash
# アプリケーション起動
./gradlew bootRun

# 別ターミナルで認可不要なAPIの確認
curl -s "http://localhost:8080/api/guest/todos" | jq .

# 認可が必要なAPIは403が返される
curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/api/pro/todos"
```

### 実装済みAPI

#### 認可不要なAPI
- `GET /api/guest/todos` - ランダムなTodoを1件返す
  - 認証不要
  - レスポンス例:
    ```json
    {
      "id": 1,
      "title": "Spring Bootの学習",
      "description": "Spring BootとKotlinでREST APIを作成する",
      "createdAt": "2025-10-26T15:00:00",
      "updatedAt": "2025-10-29T15:00:00",
      "completedAt": null
    }
    ```

#### 認可が必要なAPI（未実装）
- `GET /api/pro/todos` - 全てのTodoを返す（要: read_todos scope）
- `POST /api/pro/todos` - 新しいTodoを作成（要: write_todos scope）
- `PUT /api/pro/todos/{id}` - Todoを更新（要: write_todos scope）
- `DELETE /api/pro/todos/{id}` - Todoを削除（要: write_todos scope）