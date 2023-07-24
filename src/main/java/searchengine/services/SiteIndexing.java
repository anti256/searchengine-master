package searchengine.services;

import org.json.simple.JSONObject;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import java.sql.SQLIntegrityConstraintViolationException;

public interface SiteIndexing {
    JSONObject startSitesIndexing() throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException, SQLIntegrityConstraintViolationException;
}
