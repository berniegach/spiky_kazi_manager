/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.spikingacacia.kazi.skulist.row;

import com.android.billingclient.api.BillingClient.SkuType;
import com.spikingacacia.kazi.R;
import com.spikingacacia.kazi.billing.BillingProvider;
import java.util.ArrayList;

/**
 * Handles Ui specific to "monthly gas" - subscription row
 */
public class MonthlyDelegate extends UiManagingDelegate {
    public static final String SKU_ID = "kazi_pay_monthly";

    public MonthlyDelegate(BillingProvider billingProvider) {
        super(billingProvider);
    }

    @Override
    public @SkuType String getType() {
        return SkuType.SUBS;
    }

    @Override
    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        super.onBindViewHolder(data, holder);
        if (mBillingProvider.isMonthlySubscribed()) {
            holder.button.setText(R.string.button_own);
        } else {
            int textId = mBillingProvider.isYearlySubscribed()
                    ? R.string.button_change : R.string.button_buy;
            holder.button.setText(textId);
        }
        holder.skuIcon.setImageResource(R.drawable.gold_icon);
    }

    @Override
    public void onButtonClicked(SkuRowData data) {
        if (mBillingProvider.isYearlySubscribed()) {
            // If we already subscribed to yearly gas, launch replace flow
            ArrayList<String> currentSubscriptionSku = new ArrayList<>();
            currentSubscriptionSku.add(YearlyDelegate.SKU_ID);
            mBillingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                    currentSubscriptionSku, data.getSkuType());
        } else {
            mBillingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                    data.getSkuType());
        }
    }
}

