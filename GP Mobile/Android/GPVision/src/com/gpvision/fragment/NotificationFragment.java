package com.gpvision.fragment;

import java.util.ArrayList;

import com.gpvision.R;
import com.gpvision.adapter.NotificationAdapter;
import com.gpvision.datamodel.Notification;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class NotificationFragment extends BaseFragment {

	private ArrayList<Notification> notifications;
	private NotificationAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// test only
		notifications = new ArrayList<Notification>();
		Notification notification = new Notification();
		notification.setTitle("test1");
		notification.setMessage("message1");
		Notification notification2 = new Notification();
		notification2.setTitle("test2");
		notification2.setMessage("message2");
		notifications.add(notification);
		notifications.add(notification2);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_notification, container,
				false);
		ListView listView = (ListView) view
				.findViewById(R.id.notification_fragment_list);
		adapter = new NotificationAdapter(notifications);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSelected(position);
				adapter.notifyDataSetChanged();
			}
		});

		Button remove = (Button) view
				.findViewById(R.id.notification_fragment_remove_btn);
		remove.setOnClickListener(this);
		Button clean = (Button) view
				.findViewById(R.id.notification_fragment_clean_btn);
		clean.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.notification_fragment_remove_btn:
			int selected = adapter.getSelected();
			if (selected > -1) {
				adapter.getNotifications().remove(selected);
				adapter.setSelected(-1);
				adapter.notifyDataSetChanged();
			}
			break;
		case R.id.notification_fragment_clean_btn:
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(
					R.string.notification_fragment_alert_dialog_title_Clean)
					.setMessage(
							R.string.notification_fragment_alert_dialog_message_clean)
					.setPositiveButton(R.string.base_ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									adapter.setNotifications(null);
									adapter.notifyDataSetChanged();
								}
							}).setNegativeButton(R.string.base_cancel, null)
					.create().show();

			break;
		default:
			break;
		}
	}

}
