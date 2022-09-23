use std::path::PathBuf;
use std::time::UNIX_EPOCH;
use serde::{Serialize};

#[derive(Serialize, PartialEq, Eq)]
pub enum FileType {
    DIRECTORY,
    FILE,
}

#[derive(Serialize)]
pub struct FileEntry {
    size: usize,
    time: u64,
    pub(crate) name: String,
    pub(crate) file_type: FileType,
}

pub fn list(path: &PathBuf) -> Vec<FileEntry> {
    if let Ok(entries) = path.read_dir() {
        entries
            .filter_map(Result::ok)
            .map(|e| {
                let metadata = e.metadata().unwrap();
                let time = metadata.modified().unwrap().duration_since(UNIX_EPOCH).unwrap().as_secs();
                let name = e.file_name().to_string_lossy().into_owned();
                let file_type = e.file_type().unwrap();

                if file_type.is_dir() {
                    let size = e.path().read_dir().unwrap().count();

                    FileEntry {
                        size,
                        time,
                        name,
                        file_type: FileType::DIRECTORY,
                    }
                } else {
                    let size = metadata.len() as usize;

                    FileEntry {
                        size,
                        time,
                        name,
                        file_type: FileType::FILE,
                    }
                }
            })
            .collect::<Vec<FileEntry>>()
    } else {
        vec![]
    }
}