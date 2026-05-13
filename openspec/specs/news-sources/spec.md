## ADDED Requirements

### Requirement: 查询数据源列表
系统 SHALL 提供查询所有数据源的接口。

#### Scenario: 查询成功
- **WHEN** 客户端请求 `GET /api/sources`
- **THEN** 系统返回所有数据源列表

### Requirement: 新增数据源
系统 SHALL 提供新增数据源的接口。

#### Scenario: 新增成功
- **WHEN** 客户端携带合法数据源信息请求 `POST /api/sources`
- **THEN** 系统持久化数据源并返回 201

#### Scenario: 参数非法
- **WHEN** 客户端提交缺少必填字段的数据源信息
- **THEN** 系统返回 400

### Requirement: 更新数据源
系统 SHALL 提供更新指定数据源的接口。

#### Scenario: 更新成功
- **WHEN** 客户端请求 `PUT /api/sources/{id}` 且该 ID 存在
- **THEN** 系统更新数据源并返回 200

#### Scenario: 数据源不存在
- **WHEN** 客户端请求 `PUT /api/sources/{id}` 且该 ID 不存在
- **THEN** 系统返回 404

### Requirement: 删除数据源
系统 SHALL 提供删除指定数据源的接口。

#### Scenario: 删除成功
- **WHEN** 客户端请求 `DELETE /api/sources/{id}` 且该 ID 存在
- **THEN** 系统删除数据源并返回 204

#### Scenario: 数据源不存在
- **WHEN** 客户端请求 `DELETE /api/sources/{id}` 且该 ID 不存在
- **THEN** 系统返回 404
