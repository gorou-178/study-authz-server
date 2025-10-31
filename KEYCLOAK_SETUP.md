# Keycloak設定手順書

## 前提条件

- Docker Composeで Keycloakが起動していること
- 管理者アカウント: `admin` / `admin`
- Keycloak管理コンソール: http://localhost:9000/admin/

## 1. Keycloak管理コンソールへのログイン

1. ブラウザで http://localhost:9000/admin/ にアクセス
2. 以下の認証情報でログイン
   - Username: `admin`
   - Password: `admin`

## 2. 新しいRealmの作成

1. 左上の「master」ドロップダウンをクリック
2. 「Create Realm」ボタンをクリック
3. Realm名を入力: `study-authz`
4. 「Create」ボタンをクリック

## 3. クライアントスコープの作成

### 3.1 read_todos スコープ

1. 左メニューから「Client scopes」を選択
2. 「Create client scope」ボタンをクリック
3. 以下を入力:
   - Name: `read_todos`
   - Description: `Read access to todos`
   - Type: `Optional`
   - Protocol: `OpenID Connect`
   - Display on consent screen: `On`
4. 「Save」ボタンをクリック

### 3.2 write_todos スコープ

1. 左メニューから「Client scopes」を選択
2. 「Create client scope」ボタンをクリック
3. 以下を入力:
   - Name: `write_todos`
   - Description: `Write access to todos`
   - Type: `Optional`
   - Protocol: `OpenID Connect`
   - Display on consent screen: `On`
4. 「Save」ボタンをクリック

## 4. OAuth2クライアントの作成

### 4.1 Authorization Code Flowクライアント（リソースサーバー用）

1. 左メニューから「Clients」を選択
2. 「Create client」ボタンをクリック

#### General Settings
- Client type: `OpenID Connect`
- Client ID: `resource-server`
- Name: `Resource Server`
- Description: `Client for resource server using authorization code flow`
- 「Next」ボタンをクリック

#### Capability config
- Client authentication: `On`
- Authorization: `Off`
- Authentication flow:
  - ✅ Standard flow（Authorization Code Flow）
  - ✅ Direct access grants（Resource Owner Password Credentials）※テスト用
  - ❌ Implicit flow
  - ❌ Service accounts roles
- 「Next」ボタンをクリック

#### Login settings
- Root URL: `https://localhost:8080`
- Home URL: `https://localhost:8080`
- Valid redirect URIs: `https://localhost:8080/*`
- Valid post logout redirect URIs: `https://localhost:8080/*`
- Web origins: `https://localhost:8080`
- 「Save」ボタンをクリック

#### Credentials
1. 「Credentials」タブを選択
2. Client Secret をコピーして保存（後で使用）

#### Client Scopes
1. 「Client scopes」タブを選択
2. 「Add client scope」ボタンをクリック
3. `read_todos` を選択し、「Add」→「Optional」
4. `write_todos` を選択し、「Add」→「Optional」
5. `openid`, `profile`, `email` がデフォルトスコープに含まれていることを確認

### 4.2 Client Credentials Flowクライアント（サービス間通信用）

1. 左メニューから「Clients」を選択
2. 「Create client」ボタンをクリック

#### General Settings
- Client type: `OpenID Connect`
- Client ID: `service-client`
- Name: `Service Client`
- Description: `Client for service-to-service communication using client credentials flow`
- 「Next」ボタンをクリック

#### Capability config
- Client authentication: `On`
- Authorization: `Off`
- Authentication flow:
  - ❌ Standard flow
  - ❌ Direct access grants
  - ❌ Implicit flow
  - ✅ Service accounts roles（Client Credentials Flow）
- 「Next」ボタンをクリック

#### Login settings
- （設定不要）
- 「Save」ボタンをクリック

#### Credentials
1. 「Credentials」タブを選択
2. Client Secret をコピーして保存（後で使用）

#### Client Scopes
1. 「Client scopes」タブを選択
2. 「Add client scope」ボタンをクリック
3. `read_todos` を選択し、「Add」→「Optional」
4. `write_todos` を選択し、「Add」→「Optional」

## 5. ユーザーの作成

1. 左メニューから「Users」を選択
2. 「Add user」ボタンをクリック
3. 以下を入力:
   - Username: `testuser`
   - Email: `testuser@example.com`
   - First name: `Test`
   - Last name: `User`
   - Email verified: `On`
4. 「Create」ボタンをクリック

### パスワード設定

1. 作成したユーザーの「Credentials」タブを選択
2. 「Set password」をクリック
3. 以下を入力:
   - Password: `password`
   - Password confirmation: `password`
   - Temporary: `Off`（初期パスワード変更を不要にする）
4. 「Save」ボタンをクリック
5. 確認ダイアログで「Save password」をクリック

## 6. 設定の確認

### 6.1 Realmの設定確認

1. 左メニューから「Realm settings」を選択
2. 「General」タブで以下を確認:
   - Realm ID: `study-authz`
   - Frontend URL: （空白）

### 6.2 トークンの有効期限設定（オプション）

1. 「Realm settings」→「Tokens」タブを選択
2. 必要に応じて以下を調整:
   - Access Token Lifespan: デフォルト `5 minutes`
   - Refresh Token Lifespan: デフォルト `30 minutes`

## 7. 動作確認

### 7.1 Client Credentials Flowのテスト

```bash
# トークンエンドポイントの確認
curl -X POST http://localhost:9000/realms/study-authz/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=service-client" \
  -d "client_secret=<CLIENT_SECRET>" \
  -d "scope=read_todos write_todos"
```

### 7.2 Resource Owner Password Credentials（テスト用）

```bash
# ユーザー認証でトークン取得
curl -X POST http://localhost:9000/realms/study-authz/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=resource-server" \
  -d "client_secret=<CLIENT_SECRET>" \
  -d "username=testuser" \
  -d "password=password" \
  -d "scope=openid read_todos write_todos"
```

### 7.3 Authorization Code Flowのテスト

1. ブラウザで以下のURLにアクセス:
```
http://localhost:9000/realms/study-authz/protocol/openid-connect/auth?client_id=resource-server&redirect_uri=https://localhost:8080/callback&response_type=code&scope=openid read_todos write_todos
```

2. `testuser` / `password` でログイン
3. リダイレクトURLの `code` パラメータを取得
4. トークンと交換:

```bash
curl -X POST http://localhost:9000/realms/study-authz/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=authorization_code" \
  -d "client_id=resource-server" \
  -d "client_secret=<CLIENT_SECRET>" \
  -d "code=<CODE>" \
  -d "redirect_uri=https://localhost:8080/callback"
```

### 7.4 Refresh Token Flowのテスト

```bash
# リフレッシュトークンで新しいアクセストークンを取得
curl -X POST http://localhost:9000/realms/study-authz/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=refresh_token" \
  -d "client_id=resource-server" \
  -d "client_secret=<CLIENT_SECRET>" \
  -d "refresh_token=<REFRESH_TOKEN>"
```

## 8. 重要なエンドポイント

- Discovery URL: `http://localhost:9000/realms/study-authz/.well-known/openid-configuration`
- Authorization Endpoint: `http://localhost:9000/realms/study-authz/protocol/openid-connect/auth`
- Token Endpoint: `http://localhost:9000/realms/study-authz/protocol/openid-connect/token`
- UserInfo Endpoint: `http://localhost:9000/realms/study-authz/protocol/openid-connect/userinfo`
- Logout Endpoint: `http://localhost:9000/realms/study-authz/protocol/openid-connect/logout`
- JWKS URI: `http://localhost:9000/realms/study-authz/protocol/openid-connect/certs`

## トラブルシューティング

### トークンが取得できない場合

1. Client IDとClient Secretが正しいか確認
2. Realmが `study-authz` で正しいか確認
3. クライアントスコープが正しく設定されているか確認
4. ユーザーのパスワードが正しく設定されているか確認

### リダイレクトエラーが発生する場合

1. Valid redirect URIsに正しいURLが登録されているか確認
2. Web originsが正しく設定されているか確認
