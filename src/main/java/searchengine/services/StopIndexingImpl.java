package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StopIndexingImpl implements StopIndexing{

    @Override
    public JSONObject stopSitesIndexing() {
        return null;
    }
}
