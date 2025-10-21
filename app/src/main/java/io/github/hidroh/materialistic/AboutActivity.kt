/*
 * Copyright (c) 2015 Ha Duy Trung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.hidroh.materialistic

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import android.view.MenuItem

/**
 * Displays application's info.
 */
class AboutActivity : InjectableActivity() {
  /**
   * Sets up the activity's layout, toolbar, and displays application information with links.
   *
   * @param savedInstanceState If the activity is being re-initialized after previously being
   *                           shut down, this Bundle contains the data it most recently supplied
   *                           in onSaveInstanceState(Bundle). Otherwise, it is null.
   */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_about)
    setSupportActionBar(findViewById(R.id.toolbar))

    supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_HOME or
        ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

    var versionName = ""
    var versionCode = 0
    try {
      versionName = packageManager.getPackageInfo(packageName, 0).versionName
      versionCode = packageManager.getPackageInfo(packageName, 0).versionCode
    } catch (e: PackageManager.NameNotFoundException) {
      // do nothing
    }

    setTextWithLinks(R.id.text_application_info, getString(R.string.application_info_text, versionName, versionCode))
    setTextWithLinks(R.id.text_developer_info, getString(R.string.developer_info_text))
    setTextWithLinks(R.id.text_libraries, getString(R.string.libraries_text))
    setTextWithLinks(R.id.text_license, getString(R.string.license_text))
    setTextWithLinks(R.id.text_3rd_party_licenses, getString(R.string.third_party_licenses_text))
    setTextWithLinks(R.id.text_privacy_policy, getString(R.string.privacy_policy_text))
  }

  /**
   * Sets the text of a TextView with HTML content, enabling links.
   *
   * @param textViewResId The resource ID of the TextView to be updated.
   * @param htmlText      The HTML content to be displayed in the TextView.
   */
  private fun setTextWithLinks(@IdRes textViewResId: Int, htmlText: String) {
    AppUtils.setTextWithLinks(findViewById(textViewResId), AppUtils.fromHtml(htmlText))
  }

  /**
   * This hook is called whenever an item in your options menu is selected.
   *
   * @param item The menu item that was selected.
   * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
   */
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      finish()
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}
