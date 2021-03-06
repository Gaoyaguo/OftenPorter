package cn.xishan.oftenporter.porter.core;

import cn.xishan.oftenporter.porter.core.base.CheckHandle;
import cn.xishan.oftenporter.porter.core.base.CheckPassable;
import cn.xishan.oftenporter.porter.core.base.DuringType;
import cn.xishan.oftenporter.porter.core.base.WObject;

/**
 * Created by chenyg on 2017/1/5.
 */
class PortExecutorCheckers extends CheckHandle {

    private int currentIndex;
    private WObject wObject;
    private DuringType duringType;
    private CheckPassable[] checkPassables;
    private CheckHandle handle;
    private CheckPassable forAllCheckPassable;

    public PortExecutorCheckers(Context context, WObjectImpl wObject, DuringType duringType, CheckPassable[] checkPassables, CheckHandle handle) {
        super(wObject.url());
        currentIndex = -1;
        this.wObject = wObject;
        this.duringType = duringType;
        this.checkPassables = checkPassables;
        this.forAllCheckPassable = context.forAllCheckPassable;
        this.handle = handle;
    }

    public PortExecutorCheckers(Context context, WObjectImpl wObject, DuringType duringType, Class<? extends CheckPassable>[] cps,
                                CheckHandle handle) {
        this(context, wObject, duringType, toCheckPassables(context, cps), handle);
    }

    private static CheckPassable[] toCheckPassables(Context context, Class<? extends CheckPassable>[] cps) {
        PortContext portContext = context.portContext;
        CheckPassable[] checkPassables = new CheckPassable[cps.length];
        for (int i = 0; i < cps.length; i++) {
            CheckPassable cp = portContext.getCheckPassable(cps[i]);
            checkPassables[i] = cp;

        }
        return checkPassables;
    }

    public void check() {
        currentIndex++;
        if (forAllCheckPassable != null) {
            forAllCheckPassable.willPass(wObject, duringType, this);
        } else {
            checkOne();
        }
    }

    private void checkOne() {
        if (currentIndex < checkPassables.length) {
            checkPassables[currentIndex++].willPass(wObject, duringType, this);
        } else {
            handle.next();
        }
    }

    @Override
    public void go(Object failedObject) {
        if (failedObject != null) {
            handle.go(failedObject);
        } else {
            checkOne();
        }
    }
}
