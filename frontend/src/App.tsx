import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import ScenarioExplorer from './pages/ScenarioExplorer'
import LiveExecution from './pages/LiveExecution'
import AiInsights from './pages/AiInsights'
import ReportViewer from './pages/ReportViewer'

function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/scenarios" element={<ScenarioExplorer />} />
          <Route path="/execution" element={<LiveExecution />} />
          <Route path="/ai" element={<AiInsights />} />
          <Route path="/reports" element={<ReportViewer />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  )
}

export default App
