/*
 * Copyright (c) 2023 Ha Duy Trung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.hidroh.materialistic;

import javax.inject.Singleton;

import dagger.Component;
import io.github.hidroh.materialistic.appwidget.WidgetService;
import io.github.hidroh.materialistic.appwidget.WidgetConfigActivity;
import io.github.hidroh.materialistic.data.ItemSyncJobService;
import io.github.hidroh.materialistic.data.AlgoliaClient;
import io.github.hidroh.materialistic.data.ItemSyncService;
import io.github.hidroh.materialistic.widget.StoryRecyclerViewAdapter;

@Singleton
@Component(modules = {ApplicationModule.class, ActivityModule.class, UiModule.class})
public interface ApplicationComponent {
    void inject(MaterialisticApplication application);
    void inject(ItemSyncService itemSyncService);
    void inject(WidgetService widgetService);
    void inject(ItemSyncJobService itemSyncJobService);
    void inject(ItemActivity itemActivity);
    void inject(AccountAuthenticatorActivity accountAuthenticatorActivity);
    void inject(ComposeActivity composeActivity);
    void inject(SettingsActivity settingsActivity);
    void inject(FavoriteActivity favoriteActivity);
    void inject(PopularActivity popularActivity);
    void inject(SearchActivity searchActivity);
    void inject(AskActivity askActivity);
    void inject(BestActivity bestActivity);
    void inject(JobsActivity jobsActivity);
    void inject(ListActivity listActivity);
    void inject(NewActivity newActivity);
    void inject(ShowActivity showActivity);
    void inject(FeedbackActivity feedbackActivity);
    void inject(OfflineWebActivity offlineWebActivity);
    void inject(SubmitActivity submitActivity);
    void inject(ThreadPreviewActivity threadPreviewActivity);
    void inject(UserActivity userActivity);
    void inject(WidgetConfigActivity widgetConfigActivity);
    void inject(ListFragment listFragment);
    void inject(WebFragment webFragment);
    void inject(ItemFragment itemFragment);
    void inject(StoryRecyclerViewAdapter storyRecyclerViewAdapter);
    void inject(AlgoliaClient algoliaClient);
    void inject(AboutActivity aboutActivity);
    void inject(ReleaseNotesActivity releaseNotesActivity);
    void inject(LoginActivity loginActivity);
    void inject(MultiPageItemRecyclerViewAdapter multiPageItemRecyclerViewAdapter);
    void inject(SinglePageItemRecyclerViewAdapter singlePageItemRecyclerViewAdapter);
    void inject(SubmissionRecyclerViewAdapter submissionRecyclerViewAdapter);
    void inject(ThreadPreviewRecyclerViewAdapter threadPreviewRecyclerViewAdapter);
}
