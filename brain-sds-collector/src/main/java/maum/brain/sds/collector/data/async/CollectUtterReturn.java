package maum.brain.sds.collector.data.async;

import maum.brain.sds.data.dto.SdsResponse;
import maum.brain.sds.data.dto.cache.SdsAddCacheRequest;

public class CollectUtterReturn {
  private SdsResponse sdsResponse;
  private AsyncUtterPar asyncUtterPar;
  private SdsAddCacheRequest sdsAddCacheRequest;
  private boolean nowCacheAdd;

  public CollectUtterReturn() {
  }

  public CollectUtterReturn(SdsResponse sdsResponse,
      AsyncUtterPar asyncUtterPar,
      SdsAddCacheRequest sdsAddCacheRequest, boolean nowCacheAdd) {
    this.sdsResponse = sdsResponse;
    this.asyncUtterPar = asyncUtterPar;
    this.sdsAddCacheRequest = sdsAddCacheRequest;
    this.nowCacheAdd = nowCacheAdd;
  }

  public SdsResponse getSdsResponse() {
    return sdsResponse;
  }

  public void setSdsResponse(SdsResponse sdsResponse) {
    this.sdsResponse = sdsResponse;
  }

  public AsyncUtterPar getAsyncUtterPar() {
    return asyncUtterPar;
  }

  public void setAsyncUtterPar(AsyncUtterPar asyncUtterPar) {
    this.asyncUtterPar = asyncUtterPar;
  }

  public boolean isNowCacheAdd() { return nowCacheAdd; }

  public void setNowCacheAdd(boolean nowCacheAdd) { this.nowCacheAdd = nowCacheAdd; }

  public SdsAddCacheRequest getSdsAddCacheRequest() { return sdsAddCacheRequest; }

  public void setSdsAddCacheRequest(SdsAddCacheRequest sdsAddCacheRequest) { this.sdsAddCacheRequest = sdsAddCacheRequest; }
}
