import React from 'react';

const Dashboard = () => {
  const stats = [
    { name: 'Total Warehouses', value: '4', change: 'Active', color: 'from-blue-500/10 to-blue-600/10', text: 'text-blue-400' },
    { name: 'Active Inventory Items', value: '0', change: 'No entities created', color: 'from-amber-500/10 to-amber-600/10', text: 'text-amber-400' },
    { name: 'Incoming Shipments', value: '0', change: 'Awaiting endpoints', color: 'from-emerald-500/10 to-emerald-600/10', text: 'text-emerald-400' },
    { name: 'Low Stock Alerts', value: '0', change: 'Configured', color: 'from-red-500/10 to-red-600/10', text: 'text-red-400' },
  ];

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Welcome Header */}
      <div className="flex flex-col gap-2">
        <h1 className="text-3xl font-extrabold tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-white via-slate-200 to-slate-400">
          Dashboard Overview
        </h1>
        <p className="text-slate-400 text-sm">
          Welcome to StockFlow. Below is the system status and initial placeholder analytics structure.
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, i) => (
          <div
            key={i}
            className="glass-card rounded-2xl p-6 relative overflow-hidden transition-all duration-300 hover:-translate-y-1 hover:border-white/10 hover:shadow-xl hover:shadow-brand-500/5 group"
          >
            {/* Background gradient blur */}
            <div className={`absolute inset-0 bg-gradient-to-tr ${stat.color} opacity-40 group-hover:opacity-60 transition-opacity duration-300`} />
            
            <div className="relative z-10 space-y-4">
              <span className="text-xs font-semibold uppercase tracking-wider text-slate-400">{stat.name}</span>
              <div className="flex items-baseline justify-between">
                <span className="text-4xl font-extrabold text-white">{stat.value}</span>
                <span className={`text-xs px-2.5 py-1 rounded-full bg-white/5 font-medium ${stat.text}`}>
                  {stat.change}
                </span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Project Status Panel */}
      <div className="glass-panel rounded-2xl p-8 space-y-6">
        <h2 className="text-xl font-bold text-white border-b border-white/5 pb-4">
          StockFlow Initialization Configuration
        </h2>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 text-sm">
          <div className="space-y-4">
            <h3 className="text-brand-400 font-semibold uppercase tracking-wider text-xs">Backend Stack Configured</h3>
            <ul className="space-y-2 text-slate-300">
              <li className="flex items-center gap-2">
                <span className="w-1.5 h-1.5 rounded-full bg-brand-500" />
                Java 21 & Spring Boot 3.3.x Architecture
              </li>
              <li className="flex items-center gap-2">
                <span className="w-1.5 h-1.5 rounded-full bg-brand-500" />
                Spring Security filter chain with Stateless JWT compatibility
              </li>
              <li className="flex items-center gap-2">
                <span className="w-1.5 h-1.5 rounded-full bg-brand-500" />
                Springdoc OpenAPI (Swagger UI) ready
              </li>
              <li className="flex items-center gap-2">
                <span className="w-1.5 h-1.5 rounded-full bg-brand-500" />
                MySQL Connector & JPA properties mapped
              </li>
            </ul>
          </div>

          <div className="space-y-4">
            <h3 className="text-brand-400 font-semibold uppercase tracking-wider text-xs">Frontend Stack Configured</h3>
            <ul className="space-y-2 text-slate-300">
              <li className="flex items-center gap-2">
                <span className="w-1.5 h-1.5 rounded-full bg-brand-500" />
                React 19 & Vite Bundler setup
              </li>
              <li className="flex items-center gap-2">
                <span className="w-1.5 h-1.5 rounded-full bg-brand-500" />
                Tailwind CSS v3 utilities & responsive layout
              </li>
              <li className="flex items-center gap-2">
                <span className="w-1.5 h-1.5 rounded-full bg-brand-500" />
                React Router DOM Navigation mappings
              </li>
              <li className="flex items-center gap-2">
                <span className="w-1.5 h-1.5 rounded-full bg-brand-500" />
                Axios API agent with automatic base URL proxying
              </li>
            </ul>
          </div>
        </div>

        <div className="p-4 bg-white/5 rounded-xl border border-white/5 flex items-center justify-between text-slate-300 text-xs">
          <span>Authentication is disabled for testing. Swagger documents are fully open.</span>
          <a href="/health" className="text-brand-400 hover:underline hover:text-brand-300 transition-colors font-medium">
            Test Backend Health &rarr;
          </a>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
