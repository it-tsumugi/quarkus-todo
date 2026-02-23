"use client";

import { useState, useEffect } from "react";

type Todo = {
  id: number;
  title: string;
  completed: boolean;
};

// /api/* は next.config.ts の rewrites でバックエンドに転送される
const API = "/api";

export default function Home() {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [newTitle, setNewTitle] = useState("");

  useEffect(() => {
    fetchTodos();
  }, []);

  async function fetchTodos() {
    const res = await fetch(`${API}/todos`);
    setTodos(await res.json());
  }

  async function addTodo() {
    if (!newTitle.trim()) return;
    await fetch(`${API}/todos`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ title: newTitle }),
    });
    setNewTitle("");
    fetchTodos();
  }

  async function toggleTodo(todo: Todo) {
    await fetch(`${API}/todos/${todo.id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ ...todo, completed: !todo.completed }),
    });
    fetchTodos();
  }

  async function deleteTodo(id: number) {
    await fetch(`${API}/todos/${id}`, { method: "DELETE" });
    fetchTodos();
  }

  return (
    <main className="max-w-lg mx-auto mt-16 p-4">
      <h1 className="text-2xl font-bold mb-6">Todo</h1>

      <div className="flex gap-2 mb-6">
        <input
          type="text"
          value={newTitle}
          onChange={(e) => setNewTitle(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && addTodo()}
          placeholder="新しいタスクを入力..."
          className="flex-1 border rounded px-3 py-2 outline-none focus:ring-2 focus:ring-blue-300"
        />
        <button
          onClick={addTodo}
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
        >
          追加
        </button>
      </div>

      <ul className="space-y-2">
        {todos.map((todo) => (
          <li key={todo.id} className="flex items-center gap-3 p-3 border rounded">
            <input
              type="checkbox"
              checked={todo.completed}
              onChange={() => toggleTodo(todo)}
              className="w-4 h-4 cursor-pointer"
            />
            <span className={`flex-1 ${todo.completed ? "line-through text-gray-400" : ""}`}>
              {todo.title}
            </span>
            <button
              onClick={() => deleteTodo(todo.id)}
              className="text-red-400 hover:text-red-600 text-sm"
            >
              削除
            </button>
          </li>
        ))}
      </ul>

      {todos.length === 0 && (
        <p className="text-center text-gray-400 mt-8">タスクはありません</p>
      )}
    </main>
  );
}
