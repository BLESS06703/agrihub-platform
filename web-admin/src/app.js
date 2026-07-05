document.addEventListener('DOMContentLoaded', function() {
  const mainContent = document.getElementById('mainContent');
  const navItems = document.querySelectorAll('.nav-item');
  
  function renderPageHeader(title, subtitle) {
    return `
      <div class="page-header">
        <div class="breadcrumb">Home / ${title}</div>
        <div class="flex-between">
          <div>
            <h2>${title}</h2>
            <p>${subtitle}</p>
          </div>
          <button class="btn btn-primary">+ Add New</button>
        </div>
      </div>`;
  }
  
  function renderSummaryCards(cards) {
    return `
      <div class="stats-grid">
        ${cards.map(c => `
          <div class="stat-card">
            <div class="value" style="color: ${c.color || 'var(--primary)'}">${c.value}</div>
            <div class="label">${c.label}</div>
            ${c.change ? `<div class="change ${c.changeType}">${c.change}</div>` : ''}
          </div>
        `).join('')}
      </div>`;
  }
  
  function renderDataTable(columns, rows, actions) {
    return `
      <div class="card">
        <div class="card-header flex-between">
          <div class="search-bar">
            <input type="text" placeholder="Search..." />
            <select><option>All</option></select>
          </div>
        </div>
        <div class="table-container">
          <table>
            <thead><tr>${columns.map(c => `<th>${c}</th>`).join('')}${actions ? '<th>Actions</th>' : ''}</tr></thead>
            <tbody>
              ${rows.map(r => `
                <tr>
                  ${r.map(c => `<td>${c}</td>`).join('')}
                  ${actions ? `<td>${actions}</td>` : ''}
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
        <div class="pagination">
          <span>Showing 1-10 of ${rows.length} results</span>
          <div><button class="btn btn-sm btn-secondary">< Prev</button> <button class="btn btn-sm btn-primary">1</button> <button class="btn btn-sm btn-secondary">Next ></button></div>
        </div>
      </div>`;
  }
  
  function loadPage(page) {
    switch(page) {
      case 'dashboard': renderDashboard(); break;
      case 'farms': renderFarms(); break;
      case 'livestock': renderLivestock(); break;
      case 'marketplace': renderMarketplace(); break;
      case 'finance': renderFinance(); break;
      case 'reports': renderReports(); break;
      case 'ai': renderAI(); break;
      case 'notifications': renderNotifications(); break;
      case 'users': renderUsers(); break;
      case 'tenants': renderTenants(); break;
      case 'settings': renderSettings(); break;
      case 'analytics': renderAnalytics(); break;
      default: renderDashboard();
    }
  }
  
  function renderDashboard() {
    mainContent.innerHTML = `
      ${renderPageHeader('Dashboard', 'Platform overview and key metrics')}
      ${renderSummaryCards([
        {value: '1,247', label: 'Total Tenants', change: '+12 this month', changeType: 'up'},
        {value: '8,542', label: 'Farmers', change: '+340 this month', changeType: 'up', color: 'var(--success)'},
        {value: 'MWK 45M', label: 'Transaction Volume', change: '+18% vs last month', changeType: 'up', color: 'var(--accent)'},
        {value: '99.9%', label: 'Uptime', change: 'All systems operational', changeType: 'up', color: 'var(--info)'}
      ])}
      
      <div class="grid-2">
        <div class="card">
          <div class="card-header"><h3>Crop Production</h3></div>
          <div style="height:200px;background:var(--bg);border-radius:8px;display:flex;align-items:center;justify-content:center;color:var(--text-secondary);">Bar Chart: Maize 12,500 | Tobacco 8,200 | Tea 5,400</div>
        </div>
        <div class="card">
          <div class="card-header"><h3>Market Trends</h3></div>
          <div style="height:200px;background:var(--bg);border-radius:8px;display:flex;align-items:center;justify-content:center;color:var(--text-secondary);">Line Chart: Maize +3.2% | Soybeans +5.1%</div>
        </div>
      </div>
      
      ${renderDataTable(
        ['Tenant', 'Type', 'Country', 'Status', 'Users'],
        [
          ['Mzuzu Coffee Coop', 'Cooperative', 'MW', '<span class="badge active">Active</span>', '45'],
          ['Lilongwe Farms Ltd', 'Agribusiness', 'MW', '<span class="badge active">Active</span>', '120'],
          ['Zambia Maize Coop', 'Cooperative', 'ZM', '<span class="badge pending">Pending</span>', '0'],
          ['Ministry of Agriculture', 'Government', 'MW', '<span class="badge active">Active</span>', '230']
        ],
        '<button class="btn btn-sm btn-secondary">View</button>'
      )}
    `;
  }
  
  function renderFarms() {
    mainContent.innerHTML = `
      ${renderPageHeader('Farms', 'Manage agricultural operations')}
      ${renderSummaryCards([
        {value: '3,842', label: 'Total Farms', change: '+48 this month', changeType: 'up'},
        {value: '12,500 ha', label: 'Total Area', change: '', changeType: 'up'},
        {value: '892', label: 'Active Farms', change: '', changeType: 'up', color: 'var(--success)'},
        {value: '45', label: 'Pending Review', change: '', changeType: 'up', color: 'var(--warning)'}
      ])}
      ${renderDataTable(
        ['Farm Name', 'District', 'Size', 'Status', 'Owner', 'Created'],
        [
          ['Green Farm', 'Blantyre', '25 ha', '<span class="badge active">Active</span>', 'Scott Manda', '2026-03-15'],
          ['Sunrise Farm', 'Lilongwe', '12 ha', '<span class="badge active">Active</span>', 'Mary Banda', '2026-04-02'],
          ['Mzuzu Coffee', 'Mzimba', '50 ha', '<span class="badge active">Active</span>', 'John Phiri', '2025-11-20'],
          ['Valley Farm', 'Mulanje', '8 ha', '<span class="badge pending">Pending</span>', 'Grace Tembo', '2026-06-10']
        ],
        '<button class="btn btn-sm btn-secondary">View</button> <button class="btn btn-sm btn-secondary">Edit</button>'
      )}
    `;
  }
  
  function renderLivestock() {
    mainContent.innerHTML = `
      ${renderPageHeader('Livestock', 'Animal management and health tracking')}
      ${renderSummaryCards([
        {value: '15,230', label: 'Total Animals', change: '', changeType: 'up'},
        {value: '45', label: 'Vaccinations Due', change: 'This week', changeType: 'up', color: 'var(--warning)'},
        {value: '12', label: 'Births', change: 'This month', changeType: 'up', color: 'var(--success)'},
        {value: '3', label: 'Health Alerts', change: 'Requires attention', changeType: 'up', color: 'var(--error)'}
      ])}
      <div class="card" style="margin-bottom:16px;">
        <div class="card-header"><h3>Quick Tabs</h3></div>
        <div class="flex-row" style="gap:8px;flex-wrap:wrap;">
          <button class="btn btn-primary btn-sm">All Animals</button>
          <button class="btn btn-secondary btn-sm">Vaccinations Due</button>
          <button class="btn btn-secondary btn-sm">Health Records</button>
          <button class="btn btn-secondary btn-sm">Breeding</button>
          <button class="btn btn-secondary btn-sm">Production</button>
          <button class="btn btn-secondary btn-sm">Reports</button>
        </div>
      </div>
      ${renderDataTable(
        ['Tag ID', 'Species', 'Breed', 'Weight', 'Status', 'Location'],
        [
          ['COW-042', 'Cattle', 'Friesian', '420 kg', '<span class="badge active">Healthy</span>', 'Pen A'],
          ['PIG-015', 'Pig', 'Large White', '85 kg', '<span class="badge pending">Gestating</span>', 'Pen C'],
          ['COW-018', 'Cattle', 'Jersey', '380 kg', '<span class="badge suspended">Sick</span>', 'Pen B'],
          ['CHK-230', 'Chicken', 'Kuroiler', '-', '<span class="badge active">Active</span>', 'Coop 2']
        ],
        '<button class="btn btn-sm btn-secondary">View</button>'
      )}
    `;
  }
  
  function renderMarketplace() {
    mainContent.innerHTML = `
      ${renderPageHeader('Marketplace', 'Listings, orders, and contracts')}
      ${renderSummaryCards([
        {value: '1,245', label: 'Active Listings', change: '', changeType: 'up'},
        {value: 'MWK 28M', label: 'Trading Volume', change: '+12%', changeType: 'up', color: 'var(--accent)'},
        {value: '342', label: 'Orders', change: 'This month', changeType: 'up', color: 'var(--info)'},
        {value: '8', label: 'Disputes', change: '', changeType: 'up', color: 'var(--warning)'}
      ])}
      <div class="card" style="margin-bottom:16px;">
        <div class="card-header"><h3>Quick Tabs</h3></div>
        <div class="flex-row" style="gap:8px;flex-wrap:wrap;">
          <button class="btn btn-primary btn-sm">Listings</button>
          <button class="btn btn-secondary btn-sm">Orders</button>
          <button class="btn btn-secondary btn-sm">Offers</button>
          <button class="btn btn-secondary btn-sm">Contracts</button>
          <button class="btn btn-secondary btn-sm">Deliveries</button>
          <button class="btn btn-secondary btn-sm">Ratings</button>
        </div>
      </div>
      ${renderDataTable(
        ['Product', 'Seller', 'Price/kg', 'Quantity', 'Status'],
        [
          ['Maize Grade A', 'Mzuzu Coffee', 'MWK 800', '500 kg', '<span class="badge active">Active</span>'],
          ['Soybeans Grade B', 'Lilongwe Farms', 'MWK 1,200', '1,200 kg', '<span class="badge active">Active</span>'],
          ['Groundnuts Grade A', 'Blantyre Coop', 'MWK 1,500', '300 kg', '<span class="badge pending">Offer Made</span>']
        ],
        '<button class="btn btn-sm btn-secondary">View</button>'
      )}
    `;
  }
  
  function renderFinance() {
    mainContent.innerHTML = `
      ${renderPageHeader('Finance', 'Income, expenses, and financial reports')}
      ${renderSummaryCards([
        {value: 'MWK 45M', label: 'Total Income', change: '+18%', changeType: 'up', color: 'var(--success)'},
        {value: 'MWK 28M', label: 'Total Expenses', change: '+5%', changeType: 'down', color: 'var(--error)'},
        {value: 'MWK 17M', label: 'Net Profit', change: '38% margin', changeType: 'up', color: 'var(--accent)'},
        {value: 'MWK 12M', label: 'Outstanding Loans', change: '', changeType: 'up', color: 'var(--warning)'}
      ])}
      <div class="card" style="margin-bottom:16px;">
        <div class="card-header"><h3>Quick Tabs</h3></div>
        <div class="flex-row" style="gap:8px;flex-wrap:wrap;">
          <button class="btn btn-primary btn-sm">Income</button>
          <button class="btn btn-secondary btn-sm">Expenses</button>
          <button class="btn btn-secondary btn-sm">Loans</button>
          <button class="btn btn-secondary btn-sm">Budgets</button>
          <button class="btn btn-secondary btn-sm">Profit & Loss</button>
          <button class="btn btn-secondary btn-sm">Cash Flow</button>
          <button class="btn btn-secondary btn-sm">Taxes</button>
        </div>
      </div>
      ${renderDataTable(
        ['Date', 'Category', 'Description', 'Amount', 'Status'],
        [
          ['2026-07-01', 'Income', 'Maize Sale', '+MWK 850,000', '<span class="badge active">Completed</span>'],
          ['2026-07-02', 'Expense', 'Fertilizer Purchase', '-MWK 120,000', '<span class="badge active">Approved</span>'],
          ['2026-07-03', 'Income', 'Milk Sales', '+MWK 45,000', '<span class="badge active">Completed</span>']
        ],
        '<button class="btn btn-sm btn-secondary">View</button>'
      )}
    `;
  }
  
  function renderReports() {
    mainContent.innerHTML = `
      ${renderPageHeader('Reports', 'Analytics and exportable reports')}
      <div class="grid-2">
        ${['Yield Report', 'Livestock Report', 'Financial Report', 'Inventory Report', 'Market Report', 'Weather Report'].map(r => `
          <div class="card">
            <div class="card-header"><h3>${r}</h3></div>
            <div style="height:120px;background:var(--bg);border-radius:8px;display:flex;align-items:center;justify-content:center;color:var(--text-secondary);">Chart Preview</div>
            <div class="flex-row" style="margin-top:12px;gap:8px;">
              <button class="btn btn-sm btn-primary">Export PDF</button>
              <button class="btn btn-sm btn-secondary">Export Excel</button>
              <button class="btn btn-sm btn-secondary">View Full</button>
            </div>
          </div>
        `).join('')}
      </div>
    `;
  }
  
  function renderAI() {
    mainContent.innerHTML = `
      ${renderPageHeader('AI Intelligence', 'Disease detection, predictions, and insights')}
      ${renderSummaryCards([
        {value: '1,892', label: 'AI Conversations', change: '', changeType: 'up', color: 'var(--info)'},
        {value: '456', label: 'Disease Detections', change: '', changeType: 'up', color: 'var(--error)'},
        {value: '89%', label: 'Prediction Accuracy', change: '', changeType: 'up', color: 'var(--success)'},
        {value: '12', label: 'Active Models', change: '', changeType: 'up', color: 'var(--primary)'}
      ])}
      <div class="card" style="margin-bottom:16px;">
        <div class="card-header"><h3>AI Services</h3></div>
        <div class="flex-row" style="gap:8px;flex-wrap:wrap;">
          <button class="btn btn-primary btn-sm">Conversations</button>
          <button class="btn btn-secondary btn-sm">Disease Detection</button>
          <button class="btn btn-secondary btn-sm">Yield Predictions</button>
          <button class="btn btn-secondary btn-sm">Recommendations</button>
          <button class="btn btn-secondary btn-sm">Forecasts</button>
          <button class="btn btn-secondary btn-sm">Market Intelligence</button>
        </div>
      </div>
      <div class="card">
        <div class="card-header"><h3>Recent Disease Detections</h3></div>
        ${renderDataTable(
          ['Image', 'Disease', 'Crop', 'Confidence', 'Treatment', 'Date'],
          [
            ['[IMG]', 'Early Blight', 'Tomato', '89%', 'Copper fungicide', '2026-07-03'],
            ['[IMG]', 'Fall Armyworm', 'Maize', '94%', 'Bacillus thuringiensis', '2026-07-02'],
            ['[IMG]', 'Leaf Rust', 'Coffee', '78%', 'Triazole fungicide', '2026-07-01']
          ],
          '<button class="btn btn-sm btn-secondary">View</button>'
        )}
      </div>
    `;
  }
  
  function renderNotifications() {
    mainContent.innerHTML = `
      ${renderPageHeader('Notifications', 'System alerts and communication')}
      <div class="card" style="margin-bottom:16px;">
        <div class="card-header"><h3>Channels</h3></div>
        <div class="flex-row" style="gap:8px;">
          <button class="btn btn-primary btn-sm">Inbox</button>
          <button class="btn btn-secondary btn-sm">Announcements</button>
          <button class="btn btn-secondary btn-sm">SMS Log</button>
          <button class="btn btn-secondary btn-sm">Email Log</button>
          <button class="btn btn-secondary btn-sm">Push Notifications</button>
          <button class="btn btn-secondary btn-sm">Templates</button>
        </div>
      </div>
      ${renderDataTable(
        ['Type', 'Recipient', 'Message', 'Channel', 'Status', 'Sent'],
        [
          ['Alert', 'All Farmers', 'Rain expected tomorrow', 'Push', '<span class="badge active">Sent</span>', '2026-07-05 10:30'],
          ['Reminder', 'Scott Manda', 'Vaccination due', 'SMS', '<span class="badge active">Delivered</span>', '2026-07-05 09:00'],
          ['Market', 'Cooperative', 'Price update: Maize', 'Email', '<span class="badge pending">Queued</span>', '2026-07-05 08:00']
        ],
        '<button class="btn btn-sm btn-secondary">View</button>'
      )}
    `;
  }
  
  function renderUsers() {
    mainContent.innerHTML = `
      ${renderPageHeader('User Management', 'Users, roles, and permissions')}
      ${renderDataTable(
        ['Name', 'Email', 'Role', 'Status', 'Last Login', 'Devices'],
        [
          ['Scott Manda', 'scott@farm.com', 'Owner', '<span class="badge active">Active</span>', '2026-07-05 08:45', '2'],
          ['Mary Banda', 'mary@farm.com', 'Manager', '<span class="badge active">Active</span>', '2026-07-04 16:30', '1'],
          ['John Phiri', 'john@farm.com', 'Farmer', '<span class="badge active">Active</span>', '2026-07-03 14:00', '1'],
          ['Grace Tembo', 'grace@farm.com', 'Accountant', '<span class="badge pending">Pending</span>', '-', '0']
        ],
        '<button class="btn btn-sm btn-secondary">Edit</button> <button class="btn btn-sm btn-secondary">Roles</button>'
      )}
    `;
  }
  
  function renderTenants() {
    mainContent.innerHTML = `
      ${renderPageHeader('Tenants', 'Organization management')}
      ${renderDataTable(
        ['Tenant', 'Type', 'Country', 'Subscription', 'Users', 'Status'],
        [
          ['Mzuzu Coffee Coop', 'Cooperative', 'MW', 'PRO', '45', '<span class="badge active">Active</span>'],
          ['Lilongwe Farms', 'Agribusiness', 'MW', 'ENTERPRISE', '120', '<span class="badge active">Active</span>'],
          ['Zambia Maize Coop', 'Cooperative', 'ZM', 'STARTER', '0', '<span class="badge pending">Pending</span>'],
          ['MoA Malawi', 'Government', 'MW', 'ENTERPRISE', '230', '<span class="badge active">Active</span>']
        ],
        '<button class="btn btn-sm btn-secondary">View</button> <button class="btn btn-sm btn-secondary">Manage</button>'
      )}
    `;
  }
  
  function renderSettings() {
    mainContent.innerHTML = `
      ${renderPageHeader('Settings', 'Platform configuration')}
      <div class="grid-2">
        ${[
          {title: 'General', desc: 'Platform name, branding, defaults'},
          {title: 'Countries', desc: 'MW, ZM, TZ configurations'},
          {title: 'Languages', desc: 'English, Chichewa, Swahili'},
          {title: 'Currencies', desc: 'MWK, ZMW, TZS'},
          {title: 'Notifications', desc: 'Email, SMS, Push settings'},
          {title: 'Security', desc: 'Password policy, MFA, sessions'},
          {title: 'Storage', desc: 'File limits, backup config'},
          {title: 'Integrations', desc: 'Mobile money, weather, SMS'},
          {title: 'AI Settings', desc: 'Model versions, thresholds'}
        ].map(s => `
          <div class="card" style="cursor:pointer;">
            <h3>${s.title}</h3>
            <p style="color:var(--text-secondary);margin-top:4px;">${s.desc}</p>
          </div>
        `).join('')}
      </div>
    `;
  }
  
  function renderAnalytics() {
    mainContent.innerHTML = `
      ${renderPageHeader('Analytics', 'Advanced platform analytics')}
      <div class="grid-2">
        <div class="card"><div class="card-header"><h3>Revenue Trend</h3></div><div style="height:180px;background:var(--bg);border-radius:8px;display:flex;align-items:center;justify-content:center;">Line Chart</div></div>
        <div class="card"><div class="card-header"><h3>Farm Growth</h3></div><div style="height:180px;background:var(--bg);border-radius:8px;display:flex;align-items:center;justify-content:center;">Bar Chart</div></div>
        <div class="card"><div class="card-header"><h3>Crop Production</h3></div><div style="height:180px;background:var(--bg);border-radius:8px;display:flex;align-items:center;justify-content:center;">Area Chart</div></div>
        <div class="card"><div class="card-header"><h3>Regional Statistics</h3></div><div style="height:180px;background:var(--bg);border-radius:8px;display:flex;align-items:center;justify-content:center;">Map View</div></div>
      </div>
    `;
  }
  
  navItems.forEach(item => {
    item.addEventListener('click', function(e) {
      e.preventDefault();
      navItems.forEach(n => n.classList.remove('active'));
      this.classList.add('active');
      loadPage(this.dataset.page);
    });
  });
  
  loadPage('dashboard');
});
