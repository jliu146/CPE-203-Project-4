public abstract class Miner_Abstract extends AnimatedEntity {

    private int resourceLimit;
    private int resourceCount;

    protected int getResourceLimit() {
        return resourceLimit;
    }

    protected void setResourceLimit(int resourceLimit) {
        this.resourceLimit = resourceLimit;
    }

    protected int getResourceCount() {
        return resourceCount;
    }

    protected void setResourceCount(int resourceCount) {
        this.resourceCount = resourceCount;
    }

}
