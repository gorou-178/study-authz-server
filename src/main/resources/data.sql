-- 初期Todoデータの投入
INSERT INTO todos (title, description, created_at, updated_at, completed_at)
VALUES ('Spring Bootの学習', 'Spring BootとKotlinでREST APIを作成する', DATEADD('DAY', -5, CURRENT_TIMESTAMP()), DATEADD('DAY', -2, CURRENT_TIMESTAMP()), NULL);

INSERT INTO todos (title, description, created_at, updated_at, completed_at)
VALUES ('OAuth2の実装', 'Keycloakを使った認証・認可の実装', DATEADD('DAY', -4, CURRENT_TIMESTAMP()), DATEADD('DAY', -1, CURRENT_TIMESTAMP()), NULL);

INSERT INTO todos (title, description, created_at, updated_at, completed_at)
VALUES ('テストコードの作成', 'ユニットテストと統合テストの作成', DATEADD('DAY', -3, CURRENT_TIMESTAMP()), DATEADD('DAY', -3, CURRENT_TIMESTAMP()), DATEADD('DAY', -1, CURRENT_TIMESTAMP()));

INSERT INTO todos (title, description, created_at, updated_at, completed_at)
VALUES ('ドキュメント作成', 'API仕様書とREADMEの作成', DATEADD('DAY', -2, CURRENT_TIMESTAMP()), CURRENT_TIMESTAMP(), NULL);

INSERT INTO todos (title, description, created_at, updated_at, completed_at)
VALUES ('Docker環境構築', 'PostgreSQLとKeycloakのDocker環境構築', DATEADD('DAY', -6, CURRENT_TIMESTAMP()), DATEADD('DAY', -6, CURRENT_TIMESTAMP()), DATEADD('DAY', -5, CURRENT_TIMESTAMP()));
