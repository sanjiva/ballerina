/**
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { MENUS as WORKSPACE_MENU } from 'core/workspace/constants';
import { MENU_DEF_TYPES } from 'core/menu/constants';
import { MENUS, COMMANDS, LABELS } from './constants';

/**
 * Provides menu definitions of help plugin.
 *
 * @returns {Object[]} menu definitions.
 *
 */
export function getMenuDefinitions(plugin) {
    return [
        {
            id: MENUS.EXPORT_DIAGRAM_MENU,
            parent: WORKSPACE_MENU.FILE_MENU,
            label: LABELS.EXPORT_DIAGRAM_MENU_TITLE,
            isActive: () => {
                const { editor } = plugin.appContext;
                const activeTab = editor.getActiveEditor();
                return activeTab && activeTab.file;
            },
            icon: 'export',
            command: COMMANDS.SHOW_EXPORT_DIAGRAM_DIALOG,
            type: MENU_DEF_TYPES.ITEM,
        },
    ];
}
