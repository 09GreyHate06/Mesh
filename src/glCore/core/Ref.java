package glCore.core;

public abstract class Ref {
    private int _refCount = 0;

    protected Ref(){
        _refCount = 1;
    }

    public int getRefCount(){
        return _refCount;
    }

    public <T extends Ref> T addRef(Class<T> resource){
        T res = resource.cast(this);
        _refCount++;

        return res;
    }

    public void release(){
        _refCount--;
        if(_refCount <= 0) {
            destroy();
        }
    }

    protected abstract void destroy();
}
