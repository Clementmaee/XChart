/**
 * Copyright 2011-2013 Xeiam LLC.
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
package com.xeiam.xchart.internal.chartpart;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.Map;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.internal.interfaces.IChartPart;
import com.xeiam.xchart.internal.interfaces.IHideable;
import com.xeiam.xchart.internal.markers.Marker;
import com.xeiam.xchart.style.Series;

/**
 * @author timmolter
 */
public class ChartLegend implements IChartPart, IHideable {

  /** parent */
  private final Chart chart;

  /** the bounds */
  private Rectangle bounds;

  /**
   * Constructor
   * 
   * @param chart
   */
  public ChartLegend(Chart chart) {

    this.chart = chart;
  }

  @Override
  public void setVisible(boolean isVisible) {

  }

  @Override
  public void paint(Graphics2D g) {

    bounds = new Rectangle();
    g.setFont(chart.getStyleManager().getChartLegendFont());

    if (chart.getStyleManager().isChartLegendVisisble()) {

      Map<Integer, Series> seriesMap = chart.axisPair.seriesMap;

      // determine legend text content max width
      int legendTextContentMaxWidth = 0;
      int legendTextContentMaxHeight = 0;

      for (Integer seriesId : seriesMap.keySet()) {
        Series series = seriesMap.get(seriesId);
        TextLayout textLayout = new TextLayout(series.name, chart.getStyleManager().getChartLegendFont(), new FontRenderContext(null, true, false));
        Rectangle rectangle = textLayout.getPixelBounds(null, 0, 0);
        // System.out.println(rectangle);
        if (rectangle.getWidth() > legendTextContentMaxWidth) {
          legendTextContentMaxWidth = (int) rectangle.getWidth();
        }
        if (rectangle.getHeight() > legendTextContentMaxHeight) {
          legendTextContentMaxHeight = (int) rectangle.getHeight();
        }
      }

      // determine legend content height
      int legendContentHeight = 0;
      int maxContentHeight = Math.max(legendTextContentMaxHeight, Marker.SIZE);
      legendContentHeight = maxContentHeight * seriesMap.size() + chart.getStyleManager().getChartLegendPadding() * (seriesMap.size() - 1);

      // determine legend content width
      int legendContentWidth = (int) (3.0 * Marker.SIZE + chart.getStyleManager().getChartLegendPadding() + legendTextContentMaxWidth);

      // Draw Legend Box
      int legendBoxWidth = legendContentWidth + 2 * chart.getStyleManager().getChartLegendPadding();
      int legendBoxHeight = legendContentHeight + 2 * chart.getStyleManager().getChartLegendPadding();
      int xOffset = chart.width - legendBoxWidth - chart.getStyleManager().getChartPadding();
      int yOffset = (int) ((chart.height - legendBoxHeight) / 2.0 + chart.chartTitle.getBounds().getY() + chart.chartTitle.getBounds().getHeight());

      g.setColor(chart.getStyleManager().getChartBordersColor());
      g.drawRect(xOffset, yOffset, legendBoxWidth, legendBoxHeight);
      g.setColor(chart.getStyleManager().getChartLegendBackgroundColor());
      g.fillRect(xOffset + 1, yOffset + 1, legendBoxWidth - 1, legendBoxHeight - 1);

      // Draw legend content inside legend box
      int startx = xOffset + chart.getStyleManager().getChartLegendPadding();
      int starty = yOffset + chart.getStyleManager().getChartLegendPadding();
      for (Integer seriesId : seriesMap.keySet()) {
        Series series = seriesMap.get(seriesId);
        // paint line
        if (series.stroke != null) {
          g.setColor(series.strokeColor);
          g.setStroke(series.stroke);
          g.drawLine(startx, starty - Marker.Y_OFFSET, (int) (startx + Marker.SIZE * 3.0), starty - Marker.Y_OFFSET);
        }
        // paint marker
        if (series.marker != null) {
          g.setColor(series.markerColor);
          series.marker.paint(g, (int) (startx + (Marker.SIZE * 1.5)), starty - Marker.Y_OFFSET);
        }

        // paint series name
        g.setColor(chart.getStyleManager().getChartFontColor());
        TextLayout layout = new TextLayout(series.name, chart.getStyleManager().getChartLegendFont(), new FontRenderContext(null, true, false));
        layout.draw(g, (float) (startx + Marker.SIZE + (Marker.SIZE * 1.5) + chart.getStyleManager().getChartLegendPadding()), (starty + Marker.SIZE));
        starty = starty + legendTextContentMaxHeight + chart.getStyleManager().getChartLegendPadding();
      }

      // bounds
      bounds = new Rectangle(xOffset, yOffset, legendBoxWidth, legendBoxHeight);
      // g.setColor(Color.blue);
      // g.draw(bounds);
    }

  }

  @Override
  public Rectangle getBounds() {

    return bounds;
  }

}
