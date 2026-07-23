import { useState } from 'react';
import { Download, FileText, FileSpreadsheet, Table } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';

export function ExportBar({ onExport, isLoading, disabled = false }) {
  const [selectedFormat, setSelectedFormat] = useState('pdf');

  const handleDownload = () => {
    onExport(selectedFormat);
  };

  return (
    <div className="flex items-center space-x-2 bg-[var(--bg-secondary)] p-1.5 rounded-lg border border-[var(--border-color)] text-xs">
      <span className="text-[var(--text-muted)] font-medium px-1">Export:</span>
      
      <button
        onClick={() => setSelectedFormat('pdf')}
        className={`px-2 py-1 rounded flex items-center gap-1 transition-colors ${
          selectedFormat === 'pdf' ? 'bg-rose-500/20 text-rose-400 font-bold border border-rose-500/30' : 'text-slate-400 hover:text-slate-200'
        }`}
      >
        <FileText size={13} /> PDF
      </button>

      <button
        onClick={() => setSelectedFormat('xlsx')}
        className={`px-2 py-1 rounded flex items-center gap-1 transition-colors ${
          selectedFormat === 'xlsx' ? 'bg-emerald-500/20 text-emerald-400 font-bold border border-emerald-500/30' : 'text-slate-400 hover:text-slate-200'
        }`}
      >
        <FileSpreadsheet size={13} /> Excel
      </button>

      <button
        onClick={() => setSelectedFormat('csv')}
        className={`px-2 py-1 rounded flex items-center gap-1 transition-colors ${
          selectedFormat === 'csv' ? 'bg-sky-500/20 text-sky-400 font-bold border border-sky-500/30' : 'text-slate-400 hover:text-slate-200'
        }`}
      >
        <Table size={13} /> CSV
      </button>

      <Button
        variant="primary"
        size="xs"
        icon={Download}
        onClick={handleDownload}
        loading={isLoading}
        disabled={disabled}
      >
        Download
      </Button>
    </div>
  );
}
