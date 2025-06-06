# 示例代码包

本目录包含各个功能模块的使用示例和演示代码。

## 目录结构

- `lru_algo/` - LRU缓存算法相关示例
  - `LRUKDemo.java` - LRU-K算法的功能演示
  - `CacheFactoryDemo.java` - 缓存工厂的使用示例

## 示例代码的特点

1. **教学目的** - 这些示例代码主要用于展示API的使用方法
2. **可运行** - 每个示例都包含main方法，可以直接运行
3. **场景化** - 展示真实业务场景下的使用方式
4. **详细注释** - 包含丰富的注释说明

## 与测试代码的区别

- **示例代码**（本目录）：展示如何使用API，帮助用户快速上手
- **测试代码**（test目录）：验证功能的正确性，使用JUnit等测试框架

## 运行示例

```bash
# 运行LRU-K算法演示
java net.dreampo.java_share.examples.lru_algo.LRUKDemo

# 运行缓存工厂演示
java net.dreampo.java_share.examples.lru_algo.LRUKCacheFactoryDemo
```

## 贡献指南

如果您想添加新的示例：

1. 在对应功能的子包下创建新的Demo类
2. 类名以Demo结尾
3. 包含详细的注释和说明
4. 提供真实的使用场景
