package maum.brain.sds.collector.data.async;

import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.dto.cache.SdsAddCacheRequest;

public class CollectIntentReturn {
  private SdsResponse sdsResponse;
  private AsyncIntentPar asyncIntentPar;
  private SdsAddCacheRequest sdsAddCacheRequest;
  private boolean nowCacheAdd;

  public CollectIntentReturn() {
  }

  public CollectIntentReturn(SdsResponse sdsResponse,
      AsyncIntentPar asyncIntentPar,
      SdsAddCacheRequest sdsAddCacheRequest, boolean nowCacheAdd) {
    this.sdsResponse = sdsResponse;
    this.asyncIntentPar = asyncIntentPar;
    this.sdsAddCacheRequest = sdsAddCacheRequest;
    this.nowCacheAdd = nowCacheAdd;
  }

  public SdsResponse getSdsResponse() {
    return sdsResponse;
  }

  public void setSdsResponse(SdsResponse sdsResponse) {
    this.sdsResponse = sdsResponse;
  }

  public AsyncIntentPar getAsyncIntentPar() {
    return asyncIntentPar;
  }

  public void setAsyncIntentPar(AsyncIntentPar asyncIntentPar) { this.asyncIntentPar = asyncIntentPar; }

  public boolean isNowCacheAdd() { return nowCacheAdd; }

  public void setNowCacheAdd(boolean nowCacheAdd) { this.nowCacheAdd = nowCacheAdd; }

  public SdsAddCacheRequest getSdsAddCacheRequest() { return sdsAddCacheRequest; }

  public void setSdsAddCacheRequest(SdsAddCacheRequest sdsAddCacheRequest) { this.sdsAddCacheRequest = sdsAddCacheRequest; }
}
