-- Guest用の初期Todoデータの投入
INSERT INTO todos (title, description, is_completed, created_at, updated_at, completed_at)
VALUES ('Spring Bootの学習', 'Spring BootとKotlinでREST APIを作成する', FALSE, DATEADD('DAY', -5, CURRENT_TIMESTAMP()), DATEADD('DAY', -2, CURRENT_TIMESTAMP()), NULL);

INSERT INTO todos (title, description, is_completed, created_at, updated_at, completed_at)
VALUES ('OAuth2の実装', 'Keycloakを使った認証・認可の実装', FALSE, DATEADD('DAY', -4, CURRENT_TIMESTAMP()), DATEADD('DAY', -1, CURRENT_TIMESTAMP()), NULL);

INSERT INTO todos (title, description, is_completed, created_at, updated_at, completed_at)
VALUES ('テストコードの作成', 'ユニットテストと統合テストの作成', TRUE, DATEADD('DAY', -3, CURRENT_TIMESTAMP()), DATEADD('DAY', -3, CURRENT_TIMESTAMP()), DATEADD('DAY', -1, CURRENT_TIMESTAMP()));

INSERT INTO todos (title, description, is_completed, created_at, updated_at, completed_at)
VALUES ('ドキュメント作成', 'API仕様書とREADMEの作成', FALSE, DATEADD('DAY', -2, CURRENT_TIMESTAMP()), CURRENT_TIMESTAMP(), NULL);

INSERT INTO todos (title, description, is_completed, created_at, updated_at, completed_at)
VALUES ('Docker環境構築', 'PostgreSQLとKeycloakのDocker環境構築', TRUE, DATEADD('DAY', -6, CURRENT_TIMESTAMP()), DATEADD('DAY', -6, CURRENT_TIMESTAMP()), DATEADD('DAY', -5, CURRENT_TIMESTAMP()));

-- User用の初期Todoデータの投入
-- Test User 1: 550e8400-e29b-41d4-a716-446655440000
INSERT INTO user_todos (user_id, title, description, is_completed, created_at, updated_at, completed_at)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'プロジェクト計画', 'プロジェクトの計画を立てる', TRUE, DATEADD('DAY', -7, CURRENT_TIMESTAMP()), DATEADD('DAY', -7, CURRENT_TIMESTAMP()), DATEADD('DAY', -6, CURRENT_TIMESTAMP()));

INSERT INTO user_todos (user_id, title, description, is_completed, created_at, updated_at, completed_at)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'データベース設計', 'ER図を作成してテーブル設計を行う', FALSE, DATEADD('DAY', -5, CURRENT_TIMESTAMP()), DATEADD('DAY', -3, CURRENT_TIMESTAMP()), NULL);

INSERT INTO user_todos (user_id, title, description, is_completed, created_at, updated_at, completed_at)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'ユーザー認証実装', 'JWT認証を実装する', FALSE, DATEADD('DAY', -4, CURRENT_TIMESTAMP()), DATEADD('DAY', -1, CURRENT_TIMESTAMP()), NULL);

-- Test User 2: 6ba7b810-9dad-11d1-80b4-00c04fd430c8
INSERT INTO user_todos (user_id, title, description, is_completed, created_at, updated_at, completed_at)
VALUES ('6ba7b810-9dad-11d1-80b4-00c04fd430c8', 'フロントエンド開発', 'React でUIを作成', FALSE, DATEADD('DAY', -6, CURRENT_TIMESTAMP()), DATEADD('DAY', -2, CURRENT_TIMESTAMP()), NULL);

INSERT INTO user_todos (user_id, title, description, is_completed, created_at, updated_at, completed_at)
VALUES ('6ba7b810-9dad-11d1-80b4-00c04fd430c8', 'CI/CD構築', 'GitHub Actionsでパイプラインを構築', TRUE, DATEADD('DAY', -3, CURRENT_TIMESTAMP()), DATEADD('DAY', -3, CURRENT_TIMESTAMP()), DATEADD('DAY', -2, CURRENT_TIMESTAMP()));
