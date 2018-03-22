/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ballerinalang.compiler.tree.clauses;

import org.ballerinalang.model.tree.NodeKind;
import org.ballerinalang.model.tree.clauses.OutputRateLimitNode;
import org.wso2.ballerinalang.compiler.tree.BLangNode;
import org.wso2.ballerinalang.compiler.tree.BLangNodeVisitor;

/**
 * @see OutputRateLimitNode
 * @since 0.965.0
 *
 * Implementation of {@link OutputRateLimitNode}.
 */
public class BLangOutputRateLimit extends BLangNode implements OutputRateLimitNode {

    private String outputRateType;

    private String timeScale;

    private String rateLimitValue;

    private boolean isSnapshot;

    private boolean isEventBasedRateLimit;

    @Override
    public String getOutputRateType() {
        return outputRateType;
    }

    @Override
    public void setOutputRateType(boolean isFirst, boolean isLast, boolean isAll) {
        if (isFirst) {
            this.outputRateType = "first";
        } else if (isLast) {
            this.outputRateType = "last";
        } else if (isAll) {
            this.outputRateType = "all";
        }
    }

    @Override
    public String getRateLimitValue() {
        return rateLimitValue;
    }

    @Override
    public void setRateLimitValue(String rateLimitValue) {
        this.rateLimitValue = rateLimitValue;
    }

    @Override
    public boolean isSnapshot() {
        return isSnapshot;
    }

    @Override
    public void setSnapshot(boolean snapshot) {
        isSnapshot = snapshot;
    }

    @Override
    public boolean isEventBasedRateLimit() {
        return isEventBasedRateLimit;
    }

    @Override
    public void setEventBasedRateLimit(boolean eventBasedRateLimit) {
        isEventBasedRateLimit = eventBasedRateLimit;
    }

    @Override
    public String getTimeScale() {
        return timeScale;
    }

    @Override
    public void setTimeScale(String timeScale) {
        this.timeScale = timeScale;
    }

    @Override
    public void accept(BLangNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeKind getKind() {
        return NodeKind.OUTPUT_RATE_LIMIT;
    }
}
