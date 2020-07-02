package com.ifcc.irpc.common.config;

/**
 * @author chenghaifeng
 * @date 2020-06-30
 * @description
 */
public abstract class AbstractConfigLoader<T, U> implements IConfigLoader<T> {
    protected IConfigProvider<U> provider;

    protected AbstractConfigLoader(IConfigProvider<U> provider) {
        this.provider = provider;
    }

    @Override
    public T load(String filePath) {
        return load(getProvider().provide(filePath));
    }

    protected abstract T load(U loadSource);

    protected IConfigProvider<U> getProvider() {
        return this.provider;
    }
}
