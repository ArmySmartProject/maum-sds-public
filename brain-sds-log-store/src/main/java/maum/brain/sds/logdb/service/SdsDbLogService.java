package maum.brain.sds.logdb.service;

import maum.brain.sds.data.dto.log.SdsLogRequest;
import maum.brain.sds.data.vo.SdsAnswer;
import maum.brain.sds.data.vo.SdsEntityList;
import maum.brain.sds.data.vo.SdsIntent;
import maum.brain.sds.logdb.components.SdsLoggerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SdsDbLogService {

    private SdsLoggerDao dao;

    @Autowired
    public SdsDbLogService(){
        // do nothing, members autowired.
    }

    public void logAnything(SdsLogRequest request){
        if (request.getData().getClass().equals(SdsIntent.class)) {
            this.logIntent(request.getUtter(), (SdsIntent) request.getData());
        } else if (request.getData().getClass().equals(SdsEntityList.class)){
            this.logEntities(request.getUtter(), (SdsEntityList) request.getData());
        } else if (request.getData().getClass().equals(SdsAnswer.class)){
            this.logAnswer(request.getUtter(), (SdsAnswer) request.getData());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private void logIntent(String utter, SdsIntent intent){
        dao.logIntent(utter, intent.getIntent());
    }

    private void logEntities(String utter, SdsEntityList entities){
        dao.logEntity(utter, entities.getEntities());
    }

    private void logAnswer(String utter, SdsAnswer answer){
        dao.logAnswer(utter, answer.getAnswer());
    }
}
