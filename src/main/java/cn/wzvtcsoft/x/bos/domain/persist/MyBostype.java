package cn.wzvtcsoft.x.bos.domain.persist;


import cn.wzvtcsoft.x.bos.domain.IBostype;

class MyBostype implements IBostype {
    private final String bt;

    public MyBostype(String bt) {
        this.bt=bt;
    }
    public String  toString(){
        return this.bt;
    }
}
