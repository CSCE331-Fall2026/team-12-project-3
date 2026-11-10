import { useEffect, useState } from 'react'
import './App.css'

type MenuItem = {
  name: string
}

function App() {
  const [items, setItems] = useState<MenuItem[]>([])
  const [error, setError] = useState('')

  useEffect(() => {
    fetch('/api/customer/menuitems')
      .then((response) => {
        if (!response.ok) throw new Error('Could not load menu items')
        return response.json() as Promise<MenuItem[]>
      })
      .then(setItems)
      .catch(() => setError('Could not load menu items. Is the backend running?'))
  }, [])

  return (
    <main>
      <label htmlFor="menu-items">Choose a menu item:</label>{' '}
      <select id="menu-items" defaultValue="">
        <option value="" disabled>
          Select an item
        </option>
        {items.map((item, index) => (
          <option key={`${item.name}-${index}`} value={item.name}>
            {item.name}
          </option>
        ))}
      </select>
      {error && <p role="alert">{error}</p>}
    </main>
  )
}

export default App
