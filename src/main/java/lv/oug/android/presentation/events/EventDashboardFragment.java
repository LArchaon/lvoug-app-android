package lv.oug.android.presentation.events;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import lv.oug.android.R;
import lv.oug.android.application.ServerPullService;
import lv.oug.android.domain.Article;
import lv.oug.android.domain.Event;
import lv.oug.android.domain.EventRepository;
import lv.oug.android.presentation.BaseFragment;
import lv.oug.android.presentation.articles.ArticleDetailsFragment;

import javax.inject.Inject;

import static android.widget.AdapterView.OnItemClickListener;
import static com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import static lv.oug.android.presentation.events.EventDetailsFragment.EVENT_DETAILS_KEY;


public class EventDashboardFragment extends BaseFragment implements OnRefreshListener<ListView>, OnItemClickListener {

    @Inject
    EventRepository eventsRepository;

    @Inject
    EventsORMAdapter adapter;

    @Inject
    ServerPullService serverPullService;

    @InjectView(R.id.list_events)
    PullToRefreshListView listEvents;

    @Override
    protected int contentViewId() {
        return R.layout.event_dashboard;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        listEvents.setAdapter(adapter);
        listEvents.setOnRefreshListener(this);
        listEvents.setOnItemClickListener(this);

        listEvents.setRefreshing();
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        new AsyncTask<Void, Void, Void>() {

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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Event event = adapter.getItem(position);

        Bundle data = new Bundle();
        data.putParcelable(EVENT_DETAILS_KEY, event);

        EventDetailsFragment fragment = new EventDetailsFragment();
        fragment.setArguments(data);

        getMainActivity().changeFragment(fragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        listEvents.onRefreshComplete();
    }
}