/**
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import React from 'react';
import PropTypes from 'prop-types';
import breakpointHoc from 'src/plugins/debugger/views/BreakpointHoc';
import SimpleBBox from 'plugins/ballerina/model/view/simple-bounding-box';
import HoverGroup from 'plugins/ballerina/graphical-editor/controller-utils/hover-group';
import Node from '../../../../../model/tree/node';
import DropZone from '../../../../../drag-drop/DropZone';
import './compound-statement-decorator.css';
import Breakpoint from '../decorators/breakpoint';
import { getComponentForNodeArray } from './../../../../diagram-util';
import ArrowDecorator from '../decorators/arrow-decorator';
import TransactionFailedDecorator from './transaction-failed-decorator';

/**
 * Wraps other UI elements and provide box with a heading.
 * Enrich elements with a action box and expression editors.
 */
class TransactionStatementDecorator extends React.Component {

    /**
     * Initialize the block decorator.
     */
    constructor() {
        super();
        this.state = {
            active: 'hidden',
        };
    }

    /**
     * Call-back for when a new value is entered via expression editor.
     */
    onUpdate() {
        // TODO: implement validate logic.
    }

    /**
     * Render breakpoint element.
     * @private
     * @return {XML} React element of the breakpoint.
     */
    renderBreakpointIndicator() {
        const breakpointSize = 14;
        const { bBox } = this.props;
        const breakpointHalf = breakpointSize / 2;
        const pointX = bBox.getRight() - breakpointHalf;
        const { model: { viewState } } = this.props;
        const statementBBox = viewState.components['statement-box'];
        const pointY = statementBBox.y - breakpointHalf;
        return (
            <Breakpoint
                x={pointX}
                y={pointY}
                size={breakpointSize}
                isBreakpoint={this.props.isBreakpoint}
                onClick={() => this.props.onBreakpointClick()}
            />
        );
    }

    /**
     * Override the rendering logic.
     * @returns {XML} rendered component.
     */
    render() {
        const { bBox, isBreakpoint, isDebugHit, model } = this.props;
        const { designer } = this.context;
        const viewState = model.viewState;
        const titleH = designer.config.statement.height;
        const statementBBox = viewState.components['statement-box'];
        const gapLeft = viewState.bBox.leftMargin;
        const gapTop = designer.config.compoundStatement.padding.top;
        const bottomPadding = 10;

        // Defining coordinates of the diagram
        // (x,y)
        // (P1)
        //       |   transaction   | -------------------|(p4X)
        // (P2Y) |                     |               |
        //       |                     |(p8)           |
        //       |                     |               |---------------[failed]
        //       |                   __|__ (p12)       |
        //       |                   a = 1;            |
        //       |                     |               |
        //       |                      (p10)          |
        //       |                                     |
        //   (p7)|____________________(P6)_____________| (P5)
        //                             |

        const p1X = bBox.x - gapLeft;
        const p1Y = bBox.y + gapTop;

        const p2Y = p1Y + (titleH / 2);

        const p4X = p1X + gapLeft + statementBBox.w;

        const p8X = bBox.x;
        const p8Y = p2Y + (titleH / 2);

        const p11X = p1X;
        const p11Y = p1Y + (titleH / 2);

        let statementRectClass = 'compound-statement-rect';
        if (isDebugHit) {
            statementRectClass = `${statementRectClass} debug-hit`;
        }

        const blockHeaderHeight = viewState.components['block-header'].h -
            designer.config.compoundStatement.padding.top;

        const blockBox = {
            w: statementBBox.w + gapLeft,
            h: statementBBox.h + blockHeaderHeight,
        };
        const body = getComponentForNodeArray(this.props.model.transactionBody);

        let trainsactionTitle = 'transaction';
        if (this.props.model.condition && this.props.model.condition.value) {
            trainsactionTitle = `transaction with ${this.props.model.condition.value} retries`;
        }

        return (
            <g
                ref={(group) => {
                    this.myRoot = group;
                }}
            >
                <rect
                    x={p1X}
                    y={p1Y}
                    width={blockBox.w}
                    height={bBox.y + bBox.h - p1Y}
                    className={statementRectClass}
                    rx='5'
                    ry='5'
                />
                <text
                    x={p1X + designer.config.compoundStatement.text.padding}
                    y={p2Y}
                    className='statement-title-text-left'
                >{trainsactionTitle}
                </text>

                <DropZone
                    x={p11X}
                    y={p11Y}
                    width={statementBBox.w}
                    height={statementBBox.h}
                    baseComponent='rect'
                    dropTarget={this.props.model.body}
                    enableDragBg
                    enableCenterOverlayLine={!this.props.disableDropzoneMiddleLineOverlay}
                />
                {isBreakpoint && this.renderBreakpointIndicator()}
                {this.props.children}
                {body}
                <HoverGroup model={this.props.model} region='actionBox'>
                    <rect
                        x={p8X}
                        y={p8Y}
                        width={50}
                        height={25}
                        className='invisible-rect'
                    />
                </HoverGroup>
                <HoverGroup model={this.props.model} region='main'>
                    <rect
                        x={p8X}
                        y={bBox.y + bBox.h - 30}
                        width={50}
                        height={50}
                        className='invisible-rect'
                    />
                </HoverGroup>
                {(() => {
                    if (model.onRetryBody) {
                        const connectorEdgeBottomY = model.viewState.bBox.y + model.viewState.bBox.h - bottomPadding;
                        const connectorEdgeTopX = p4X;
                        const connectorEdgeBottomX = p4X;
                        return (
                        [<TransactionFailedDecorator
                            bBox={model.onRetryBody.viewState.bBox}
                            model={model.onRetryBody}
                            body={model.onRetryBody}
                            connectorStartX={connectorEdgeTopX}
                        />,
                            <line
                                x1={model.onRetryBody.viewState.bBox.x}
                                y1={connectorEdgeBottomY}
                                x2={connectorEdgeBottomX}
                                y2={connectorEdgeBottomY}
                                className='flowchart-background-empty-rect'
                            />,
                            <ArrowDecorator
                                start={{
                                    x: p4X + 1,
                                    y: bBox.y + bBox.h - bottomPadding,
                                }}
                                end={{
                                    x: p4X,
                                    y: bBox.y + bBox.h - bottomPadding,
                                }}
                                classNameArrow='flowchart-action-arrow'
                                classNameArrowHead='flowchart-action-arrow-head'
                            />,
                        ]
                        );
                    }
                    return null;
                }
                )()}
            </g >
        );
    }
}

TransactionStatementDecorator.defaultProps = {
    isDebugHit: false,
    disableDropzoneMiddleLineOverlay: false,
    disableButtons: {
        debug: false,
        delete: false,
        jump: false,
    },
    children: [],
};

TransactionStatementDecorator.propTypes = {
    model: PropTypes.instanceOf(Node).isRequired,
    children: PropTypes.arrayOf(PropTypes.node),
    bBox: PropTypes.instanceOf(SimpleBBox).isRequired,
    onBreakpointClick: PropTypes.func.isRequired,
    isBreakpoint: PropTypes.bool.isRequired,
    isDebugHit: PropTypes.bool,
    disableDropzoneMiddleLineOverlay: PropTypes.bool,
};

TransactionStatementDecorator.contextTypes = {
    getOverlayContainer: PropTypes.instanceOf(Object).isRequired,
    environment: PropTypes.instanceOf(Object).isRequired,
    editor: PropTypes.instanceOf(Object).isRequired,
    mode: PropTypes.string,
    designer: PropTypes.instanceOf(Object),
};

export default breakpointHoc(TransactionStatementDecorator);
