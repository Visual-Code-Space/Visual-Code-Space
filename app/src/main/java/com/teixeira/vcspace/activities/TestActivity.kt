/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.teixeira.vcspace.activities

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.activities.base.BaseComposeActivity
import kotlin.math.pow
import kotlin.math.sqrt

class TestActivity : BaseComposeActivity() {
  @SuppressLint("SetJavaScriptEnabled")
  @Composable
  override fun MainScreen() {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()
    ) {
      VisualScriptingUI()
    }
  }
}

@Composable
fun VisualScriptingUI() {
  var nodes by remember { mutableStateOf(listOf<NodeData>()) }
  var connections by remember { mutableStateOf(listOf<ConnectionData>()) }
  var nextNodeId = remember { mutableStateOf(0) }
  var draggingConnection by remember { mutableStateOf<Pair<Int, Offset>?>(null) }
  val nodePositions = remember { mutableStateMapOf<Int, LayoutCoordinates>() }
  val isConnecting = remember { mutableStateOf(false) } // Track connection state

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(onClick = {
        nodes = nodes + NodeData(nextNodeId.value, "Node ${nextNodeId.value}")
        nextNodeId.value++
      }) {
        Icon(Icons.Filled.Add, contentDescription = "Add Node")
      }
    }
  ) { padding ->
    Box(Modifier
      .padding(padding)
      .fillMaxSize()) {
      NodeList(
        nodes = nodes,
        draggingConnection = draggingConnection,
        nodePositions = nodePositions,
        onDelete = { nodeId ->
          nodes = nodes.filter { it.id != nodeId }
          connections = connections.filter { it.from != nodeId && it.to != nodeId }
        },
        onPositioned = { id, coordinates ->
          nodePositions[id] = coordinates
        },
        onConnectStart = { id, offset ->
          draggingConnection = Pair(id, offset)
          isConnecting.value = true
        },
        onConnectEnd = { from, to ->
          if (to != null && from != to) {
            connections = connections + ConnectionData(from!!, to)
          }
          draggingConnection = null
          isConnecting.value = false
        },
        isConnecting = isConnecting.value
      )

      ConnectionLines(connections, nodePositions, draggingConnection)
    }
  }
}

data class NodeData(val id: Int, val name: String)
data class ConnectionData(val from: Int, val to: Int)

@Composable
fun NodeList(
  nodes: List<NodeData>,
  draggingConnection: Pair<Int, Offset>?,
  nodePositions: Map<Int, LayoutCoordinates>,
  onDelete: (Int) -> Unit,
  onPositioned: (Int, LayoutCoordinates) -> Unit,
  onConnectStart: (Int, Offset) -> Unit,
  onConnectEnd: (Int?, Int?) -> Unit, // Corrected parameter type
  isConnecting: Boolean
) {
  LazyColumn(Modifier.fillMaxSize()) {
    items(nodes) { node ->
      Node(
        node,
        draggingConnection,
        nodePositions,
        onDelete,
        onPositioned,
        onConnectStart,
        onConnectEnd,
        isConnecting
      )
    }
  }
}

fun Modifier.nodeDrag(
  onDragStart: (Offset) -> Unit,
  onDrag: (Offset) -> Unit,
  onDragEnd: () -> Unit
): Modifier = composed {
  var startOffset by remember { mutableStateOf(Offset.Zero) }
  pointerInput(Unit) {
    detectDragGestures(
      onDragStart = { offset ->
        startOffset = offset
        onDragStart(offset)
      },
      onDrag = { change, dragAmount ->
        change.consume()
        onDrag(dragAmount)
      },
      onDragEnd = {
        onDragEnd()
      }
    )
  }
}

@Composable
fun Node(
  node: NodeData,
  draggingConnection: Pair<Int, Offset>?,
  nodePositions: Map<Int, LayoutCoordinates>,
  onDelete: (Int) -> Unit,
  onPositioned: (Int, LayoutCoordinates) -> Unit,
  onConnectStart: (Int, Offset) -> Unit,
  onConnectEnd: (Int?, Int?) -> Unit, // Corrected parameter type
  isConnecting: Boolean
) {
  var offset by remember { mutableStateOf(Offset.Zero) }
  var isOverConnector by remember { mutableStateOf(false) }
  val density = LocalDensity.current

  Row(
    Modifier
      .fillMaxWidth()
      .padding(8.dp)
      .offset(x = offset.x.dp, y = offset.y.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(if (isOverConnector && isConnecting) Color.Green else Color.LightGray) // Highlight when over connector
      .padding(8.dp)
      .onGloballyPositioned { coordinates ->
        onPositioned(node.id, coordinates)
      }
      .nodeDrag(
        onDragStart = { /* No-op for now */ },
        onDrag = { delta -> offset += delta },
        onDragEnd = { /* No-op for now */ }
      ),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = node.name, Modifier.weight(1f))
    Box(
      Modifier
        .clickable { onConnectStart(node.id, Offset.Zero) }
        .size(16.dp)
        .background(if (isOverConnector && isConnecting) Color.Green else Color.Red)
        .onGloballyPositioned { coordinates ->
          if (draggingConnection != null) {
            val distance = calculateDistance(
              nodePositions[draggingConnection.first]!!.boundsInRoot().center,
              coordinates.boundsInRoot().center
            )
            isOverConnector = with(density) { distance < 32.dp.toPx() }
            if (!isConnecting) {
              onConnectEnd(
                draggingConnection.first,
                if (isOverConnector) node.id else null
              ) // Pass both from and to
            }
          }
        }
    )
    IconButton(onClick = { onDelete(node.id) }) {
      Icon(Icons.Filled.Delete, contentDescription = "Delete")
    }
  }
}

fun calculateDistance(p1: Offset, p2: Offset): Float {
  return sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))
}

@Composable
fun ConnectionLines(
  connections: List<ConnectionData>,
  nodePositions: Map<Int, LayoutCoordinates>,
  draggingConnection: Pair<Int, Offset>?
) {
  Canvas(Modifier.fillMaxSize()) {
    connections.forEach { connection ->
      val start = nodePositions[connection.from]?.boundsInRoot()?.center
      val end = nodePositions[connection.to]?.boundsInRoot()?.center

      if (start != null && end != null) {
        drawPath(
          path = Path().apply {
            moveTo(start.x, start.y)
            cubicTo(
              start.x + 100, start.y,
              end.x - 100, end.y,
              end.x, end.y
            )
          },
          color = Color.Black,
          style = Stroke(width = 2.dp.toPx())
        )
      }
    }

    draggingConnection?.let { (from, offset) ->
      val start = nodePositions[from]?.boundsInRoot()?.center
      if (start != null) {
        drawLine(
          color = Color.Gray,
          start = start,
          end = offset,
          strokeWidth = 2.dp.toPx()
        )
      }
    }
  }
}
