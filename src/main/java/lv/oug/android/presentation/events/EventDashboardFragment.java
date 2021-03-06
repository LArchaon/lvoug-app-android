package lv.oug.android.presentation.events;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import lv.oug.android.R;
import lv.oug.android.application.ServerPullService;
import lv.oug.android.domain.Event;
import lv.oug.android.domain.EventRepository;
import lv.oug.android.infrastructure.common.ClassLogger;
import lv.oug.android.infrastructure.common.NetworkService;
import lv.oug.android.presentation.BaseFragment;

import javax.inject.Inject;

import static android.widget.AdapterView.OnItemClickListener;
import static com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import static lv.oug.android.presentation.events.EventDetailsFragment.EVENT_DETAILS_KEY;


public class EventDashboardFragment extends BaseFragment implements OnRefreshListener<ListView>, OnItemClickListener {

    private final ClassLogger logger = new ClassLogger(EventDashboardFragment.class);

    @Inject
    EventRepository eventsRepository;

    @Inject
    EventsORMAdapter adapter;

    @Inject
    ServerPullService serverPullService;

    @Inject
    NetworkService networkService;

    @InjectView(R.id.list_events)
    PullToRefreshListView listEvents;

    @InjectView(R.id.empty_view)
    LinearLayout emptyView;

    @Override
    protected int contentViewId() {
        return R.layout.event_dashboard;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        listEvents.setAdapter(adapter);
        listEvents.setOnRefreshListener(this);
        listEvents.setOnItemClickListener(this);
        listEvents.setEmptyView(emptyView);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        try {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
                    if (!networkService.internetAvailable()) {
                        Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                protected Void doInBackground(Void... params) {
                    serverPullService.loadAndSaveEvents();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    listEvents.onRefreshComplete();
                    adapter.notifyDataSetChanged();
                }
            }.execute();
        } catch (Exception e) {
            logger.e("Exception during server connection", e);
            Toast.makeText(getActivity(), R.string.failed_to_connect_to_server, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        listEvents.setRefreshing();
        onRefresh(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - 1; // hack to Pull to Refresh silly problem
        Event event = adapter.getItem(position);

        Bundle data = new Bundle();
        data.putParcelable(EVENT_DETAILS_KEY, event);

        EventDetailsFragment fragment = new EventDetailsFragment();
        fragment.setArguments(data);

        getMainActivity().changeFragment(fragment);
    }
}
