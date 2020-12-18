package Utils.insert;

import DataCreater.FiledCreater.baseFiledCreater;
import Utils.Factorys.FiledCreaterFactory;
import Utils.BaseProperties;
import dataStructure.RuntimeEnvironment;
import dataStructure.TableStructure;
import main.control.start;

import java.util.Objects;

/**
 * 产生独立线程和数据格式判断
 */
public class SQLCreaterRunner implements Runnable {

    private int makenumber;

    private baseFiledCreater bc;
    private int partCreatemax;
    private start upStare;

    public SQLCreaterRunner(TableStructure tableStructure, int makenumber, start upStare, RuntimeEnvironment env) {
        this.makenumber = makenumber;
        this.upStare=upStare;
        bc= FiledCreaterFactory.getFiledCreater(tableStructure,env);
        partCreatemax=Objects.equals(BaseProperties.getEnvironment("longerInsert"), "true")?
                Integer.valueOf(BaseProperties.getEnvironment("longerInsertNumber")):1;
    }

    @Override
    public void run() {
        while(makenumber>=partCreatemax){
            String toWrite = bc.makeinsert(partCreatemax);
            upStare.send(toWrite);
            makenumber-=partCreatemax;
        }

        if(makenumber>0) {
            upStare.send(bc.makeinsert(makenumber));
        }
    }
}
