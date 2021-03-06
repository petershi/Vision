package com.gpvision.api.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;

import com.gpvision.api.APIResponse;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.datamodel.Index;
import com.gpvision.http.HttpRequest;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.Environment;
import com.gpvision.utils.ImageCacheUtil;
import com.gpvision.utils.LocalDataBuffer;
import com.gpvision.utils.LogUtil;

public class DownLoadImageRequest<RESPONSE extends APIResponse> extends
		AsyncTask<Void, Void, Void> {

	private APIResponseHandler<RESPONSE> responseHandler;

	private HashMap<Integer, ArrayList<Index>> indexMap;
	private DownLoadStatusCallBack callBack;

	public DownLoadImageRequest(HashMap<Integer, ArrayList<Index>> indexMap) {
		super();
		this.indexMap = indexMap;
	}

	public void start(APIResponseHandler<RESPONSE> handler) {
		this.responseHandler = handler;
		execute();
	}

	public void setCallBack(DownLoadStatusCallBack callBack) {
		this.callBack = callBack;
	}

	@Override
	protected Void doInBackground(Void... params) {
		String userToken = null;
		if (LocalDataBuffer.getInstance().getAccount() != null) {
			userToken = LocalDataBuffer.getInstance().getAccount()
					.getUserToken();
		}
		Environment environment = LocalDataBuffer.getInstance()
				.getEnvironment();
		StringBuilder builder = new StringBuilder();
		builder.append("https://");
		builder.append(environment.getHost());
		if (!AppUtils.isEmpty(environment.getBasePath())) {
			builder.append(environment.getBasePath());
		}
		String baseUrl = builder.toString();
		int size = indexMap.size(), indexKey = 0, n = 0;

		while (n < size) {
			if (indexMap.containsKey(indexKey)) {
				ArrayList<Index> indexs = indexMap.get(indexKey);
				for (Index index : indexs) {
					String url = index.getImageUrl();
					if (ImageCacheUtil.isFileExists(ImageCacheUtil
							.getChildDir(url))) {
						continue;
					}
					HttpRequest request = new HttpRequest(baseUrl + url);
					LogUtil.logI(baseUrl + url);
					request.addHeader("endUserToken", userToken);
					String childDir = ImageCacheUtil.getChildDir(url);
					InputStream inputStream;
					try {
						inputStream = request.getStream();
						if (inputStream != null)
							ImageCacheUtil.write2SDCard(childDir, inputStream);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				n++;
				indexKey++;
			} else {
				indexKey++;
			}
			callBack.downLoadStatus(indexKey);
		}
		callBack.downLoadStatus(Integer.MAX_VALUE);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		responseHandler.handleResponse(null);
	}

	public interface DownLoadStatusCallBack {
		public void downLoadStatus(int index);
	}
}
